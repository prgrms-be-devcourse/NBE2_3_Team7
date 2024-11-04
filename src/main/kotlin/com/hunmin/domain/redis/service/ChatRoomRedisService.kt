package com.hunmin.domain.redis.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hunmin.domain.dto.chat.ChatRoomRequestDTO
import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.ChatMessage
import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.chat.ChatRoomException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.redis.entity.ChatRoomRedis
import com.hunmin.domain.redis.repository.ChatMessageRedisRepository
import com.hunmin.domain.redis.repository.ChatRoomRedisRepository
import com.hunmin.domain.redis.repository.search.ChatRoomRedisSearchImpl
import com.hunmin.domain.repository.ChatMessageRepository
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.NotificationService
import org.hibernate.query.sqm.tree.SqmNode.log
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Service
@Transactional
class ChatRoomRedisService(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val memberRepository: MemberRepository,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters,
    private val modelMapper: ModelMapper,
    private val chatRoomRedisRepository: ChatRoomRedisRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRedisRepository: ChatMessageRedisRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val followRepository: FollowRepository,
) : ChatRoomRedisSearchImpl(chatRoomRedisRepository) {
    // 채팅방 생성
    fun createChatRoomByNickName(partnerName: String, myEmail: String): ChatRoomRequestDTO {
        try {
            val partner = memberRepository.findByNickname(partnerName)
            val partnerId = partner.memberId
            val me: Member = memberRepository.findByEmail(myEmail)

            // 중복이름으로 무언가 하려는 시도 차단
            if (partnerName == me.nickname) throw ChatRoomException.FAILED_REGISTER.get()

            // 중복확인 - 레디스
            val redisChatRooms = chatRoomRedisRepository.findAll()
            log.info("redisChatRooms_생성 $redisChatRooms")
            for (chatRooms in redisChatRooms) {
                if (chatRooms.member.nickname == me.nickname && chatRooms.partner.nickname == partner.nickname) {
                    throw ChatRoomException.CHATROOM_ALREADY_EXIST.get()
                } else if (chatRooms.member.nickname == partner.nickname && chatRooms.partner.nickname == me.nickname) {
                    throw ChatRoomException.CHATROOM_ALREADY_EXIST.get()
                }
            }
            //중복확인 - DB
            val dbChatRooms = getDbChatRoomsByMember(me)
            log.info("dbChatRooms_생성 $dbChatRooms")
            for (dbChatRoom in dbChatRooms) {
                if (dbChatRoom.nickName == me.nickname && dbChatRoom.partnerName == partnerName) {
                    throw ChatRoomException.CHATROOM_ALREADY_EXIST.get()
                } else if (dbChatRoom.nickName == partner.nickname && dbChatRoom.nickName == me.nickname) {
                    throw ChatRoomException.CHATROOM_ALREADY_EXIST.get()
                }
            }

            val memberDTO = MemberDTO(
                memberId = me.memberId, email = me.email,
                nickname = me.nickname,
                level = me.level,
                password = me.password,
                country = me.country,
                image = me.image
            )

            val partnerDTO = MemberDTO(
                memberId = partner.memberId, email = partner.email,
                nickname = partner.nickname,
                level = partner.level,
                password = partner.password,
                country = partner.country,
                image = partner.image
            )

            // redis로 id값 자동 증가시키기
            val increasedId = redisTemplate.opsForValue().increment("chatRoomRedisId")!!

            // StackOverFlow 해결 =memberDTO사용
            val chatRoom: ChatRoomRedis = ChatRoomRedis(id = increasedId, member = memberDTO, partner = partnerDTO)

            // redis에 저장
            chatRoomRedisRepository.save(chatRoom)
            val allChatRooms = chatRoomRedisRepository.findAll()
            //채팅 준비
            val chatMessagesList: MutableList<ChatMessage> = mutableListOf()
            // 캐싱전략 설계 10개 이상이면 -> DB저장
            if ((increasedId % 10).toInt() == 0) {
                for (chatRooms in allChatRooms) {
                    // ChatRoom DB에 저장
                    val newMember = modelMapper.map(chatRooms.member, Member::class.java)
                    val newPartner = modelMapper.map(chatRooms.partner, Member::class.java)

                    val newChatRoom = ChatRoom(
                        chatRoomId = chatRooms.id,
                        member = newMember,
                        partner = newPartner,
                        chatMessage = mutableListOf(),
                        userCount = chatRooms.userCount
                    )

                    //채팅도 함께 DB저장
                    val chatMessages = chatMessageRedisRepository.findAll()
                    log.info("chatMessages는? $chatMessages")

                    for (redisChatMessage in chatMessages) {
                        if (redisChatMessage.chatRoom.id == chatRooms.id) {
                            val newChatMessage = ChatMessage(
                                chatRoom = newChatRoom, message = redisChatMessage.message,
                                member = newMember,
                                type = redisChatMessage.type
                            )
                            log.info("newChatMessage는? $newChatMessage")
                            // DB저장
                            val savedDbChatMessage = chatMessageRepository.save(newChatMessage)
                            chatMessagesList.add(savedDbChatMessage)
                            log.info("save는? $savedDbChatMessage")
                        }
                    }
                    val realChatRoom = newChatRoom.copy(
                        chatMessage = chatMessagesList
                    )
                    chatMessagesList.clear()
                    // DB저장
                    chatRoomRepository.save(realChatRoom)
                }
                // redis 저장소 비우기
                chatRoomRedisRepository.deleteAll()
                chatMessageRedisRepository.deleteAll()
            }
            //알림
            val foundFollow = followRepository.findByMemberId(me.memberId, partnerId)
            if (foundFollow.isPresent) {
                val follow = foundFollow.get()
                if (!follow.isBlock && follow.notification) {
                    val notificationSendDTO = NotificationSendDTO(
                        memberId = partnerId,
                        message = "[" + me.nickname + "]님이 ${partner.nickname}님을 채팅방에 초대하였습니다.",
                        notificationType = NotificationType.CHAT,
                        url = "/chat-room/" + chatRoom.id
                    )
                    log.info("notificationSendDTO $notificationSendDTO")
                    notificationService.send(notificationSendDTO)
                    val emitterId = partnerId.toString() + "_"
                    val emitter = sseEmitters.findSingleEmitter(emitterId)

                    if (emitter != null) {
                        try {
                            emitter.send(chatRoom)
                        } catch (e: IOException) {
                            log.error("Error sending chat room notification to client via SSE: {}", e)
                            sseEmitters.delete(emitterId)
                        }
                    }
                }
            }
            return ChatRoomRequestDTO(
                chatRoomId = chatRoom.id,
                memberId = me.memberId, nickName = me.nickname,
                partnerName = partnerName,
                createdAt = chatRoom.createdAt
            )
        } catch (e: Exception) {
            throw Exception("채팅룸 만들기 실패 ${e.message}")
        }
    }

    // 채팅방 삭제
    fun deleteChatRoom(chatRoomId: Long): Boolean {
        try {
            log.info("chatRoomId삭제 $chatRoomId")
            val redisChatRoom = chatRoomRedisRepository.findById(chatRoomId)
            log.info("redisChatRoomㄴㄴ $redisChatRoom")
            if (redisChatRoom.isEmpty) {
                val foundChatRoom = chatRoomRepository.findById(chatRoomId)
                log.info("foundChatRoomㄴㄴ $foundChatRoom")
                if (foundChatRoom.isEmpty) {
                    return false
                }
                chatRoomRepository.deleteById(chatRoomId)
                return true
            } else {
                chatRoomRedisRepository.deleteById(chatRoomId)
                return true
            }
        } catch (e: Exception) {
            log.info("채팅방 삭제에 실패하였습니다. ${e.message}")
            throw ChatRoomException.NOT_FOUND.get()
        }
    }

    //관련 채팅방 조회
    fun findRoomByEmail(email: String, pageRequestDTO: PageRequestDTO): Page<ChatRoomRequestDTO> {
        try {
            val me = memberRepository.findByEmail(email)

            val redisChatRooms = getRedisChatRoomsByNickName(me.nickname)
            log.info("redisChatRooms = $redisChatRooms")
            val dbChatRooms = getDbChatRoomsByMember(me)
            log.info("dbChatRooms = $dbChatRooms")

            // 새로운 List 생성 -> dbchatRoom , redisChatRoom list에 넣기
            val chatRoomDTOs: MutableList<ChatRoomRequestDTO> = mutableListOf()
            chatRoomDTOs.addAll(dbChatRooms)
            chatRoomDTOs.addAll(redisChatRooms)

            // 페이지네이션 처리를 위해 전체 리스트 정렬
            chatRoomDTOs.sortBy { it.partnerName }

            // PageRequestDTO로 받은 페이지 정보에 맞게 페이지네이션 수행
            val pageable = pageRequestDTO.getPageable(Sort.by("partnerName").ascending())
            val start = pageable.offset.toInt()
            val end = (start + pageable.pageSize).coerceAtMost(chatRoomDTOs.size)
            val pagedChatRoomDTOs = chatRoomDTOs.subList(start, end)

            return PageImpl(pagedChatRoomDTOs, pageable, chatRoomDTOs.size.toLong())
        } catch (e: Exception) {
            log.error("채팅방 불러오기에 실패하였습니다 ${e.message}")
            throw ChatRoomException.FAILED_READ_ROOMS.get()
        }

    }

    fun getRedisChatRoomsByNickName(nickName: String): List<ChatRoomRequestDTO> {
        // 새로운 List 생성 -> 관련채팅방 모두 넣기
        val chatRoomDTOs: MutableList<ChatRoomRequestDTO> = mutableListOf()
        log.info("chatRoomDTOs1 = $chatRoomDTOs")

        val chatRoomMe = findByMemberNickname(nickName)
        val ChatRoomPart = findByPartnerNickname(nickName)

        log.info("chatRoomMe = $chatRoomMe, ChatRoomPart = $ChatRoomPart")
        // 내가 owner인 채팅룸 -> dto로 변환 후 삽입
        for (chatRooms in chatRoomMe) {
            val chatRoomRequest = objectMapper.convertValue(
                chatRooms,
                ChatRoomRedis::class.java
            )
            log.info("chatRoomRequest1 = $chatRoomRequest")
            val chatRoomRequestDTO = ChatRoomRequestDTO(
                chatRoomId = chatRoomRequest.id,
                memberId = chatRoomRequest.member.memberId,
                nickName = chatRoomRequest.member.nickname,
                partnerName = chatRoomRequest.partner.nickname,
                createdAt = chatRoomRequest.createdAt
            )
            log.info("chatRoomRequestDTO2 = $chatRoomRequestDTO")
            chatRoomDTOs.add(chatRoomRequestDTO)
        }
        // 내가 partner인 채팅룸 -> dto로 변환 후 삽입
        for (chatRooms in ChatRoomPart) {
            val chatRoomRequest = objectMapper.convertValue(
                chatRooms,
                ChatRoomRedis::class.java
            )
            log.info("chatRoomRequest2 = $chatRoomRequest")
            val chatRoomRequestDTO = ChatRoomRequestDTO(
                chatRoomId = chatRoomRequest.id,
                memberId = chatRoomRequest.partner.memberId,
                nickName = chatRoomRequest.partner.nickname,
                partnerName = chatRoomRequest.member.nickname,
                createdAt = chatRoomRequest.createdAt
            )
            log.info("chatRoomRequestDTO3 = $chatRoomRequestDTO")
            chatRoomDTOs.add(chatRoomRequestDTO)
        }
        return chatRoomDTOs
    }

    fun getDbChatRoomsByMember(member: Member): List<ChatRoomRequestDTO> {
        val chatRoomDTOs: MutableList<ChatRoomRequestDTO> = mutableListOf()
        val chatRoomsMe = chatRoomRepository.findChatRoomByMember(member.memberId)
        log.info("chatRoomsMe3 = $chatRoomsMe")
        val chatRoomsPart = chatRoomRepository.findChatRoomByPartner(member.memberId)
        log.info("chatRoomsPart3 = $chatRoomsPart")
        chatRoomDTOs.addAll(chatRoomsMe)
        chatRoomDTOs.addAll(chatRoomsPart)
        return chatRoomDTOs
    }
}

