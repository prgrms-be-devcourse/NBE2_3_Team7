package com.hunmin.domain.redis.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hunmin.domain.dto.chat.ChatRoomRequestDTO
import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.chat.ChatRoomException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.redis.entity.ChatRoomRedis
import com.hunmin.domain.redis.repository.ChatRoomRedisRepository
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.NotificationService
import io.jsonwebtoken.io.DeserializationException
import io.lettuce.core.RedisCommandTimeoutException
import org.hibernate.TransactionException
import org.hibernate.query.sqm.tree.SqmNode.log
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import org.springframework.data.redis.RedisConnectionFailureException
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
) {
    // 채팅방 생성
    fun createChatRoomByNickName(partnerName: String, myEmail: String): ChatRoomRequestDTO {
        try {
            val partner = memberRepository.findByNickname(partnerName)
            val partnerId = partner.memberId
            val me: Member = memberRepository.findByEmail(myEmail)

            // 중복확인
            val redisChatRooms = chatRoomRedisRepository.findAll()
            for (chatRooms in redisChatRooms) {
                if (chatRooms.member.nickname == me.nickname && chatRooms.partner.nickname == partner.nickname) {
                    throw ChatRoomException.CHATROOM_ALREADY_EXIST.get()
                } else if (chatRooms.member.nickname == partner.nickname && chatRooms.partner.nickname == me.nickname) {
                    throw ChatRoomException.CHATROOM_ALREADY_EXIST.get()
                }
            }

            var memberDTO = MemberDTO(
                memberId = me.memberId, email = me.email,
                nickname = me.nickname,
                level = me.level,
                password = me.password,
                country = me.country,
                image = me.image
            )

            var partnerDTO = MemberDTO(
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
            // 캐싱전략 설계 10개 이상이면 -> DB저장
            if ((increasedId % 10).toInt() == 0) {
                for (chatRooms in allChatRooms) {
                    // 새로운 ChatRoom 생성
                    val newMember = modelMapper.map(chatRooms.member, Member::class.java)
                    val newPartner = modelMapper.map(chatRooms.partner, Member::class.java)
                    val newChatRoom = ChatRoom(
                        chatRoomId = chatRooms.id,
                        member = newMember,
                        partner = newPartner,
                        chatMessage = chatRooms.chatMessage,
                        userCount = chatRooms.userCount
                    )
                    // DB저장
                    chatRoomRepository.save(newChatRoom)
                }
                // redis 저장소 비우기
                chatRoomRedisRepository.deleteAll()
            }
            //알림
            val notificationSendDTO = NotificationSendDTO(
                message = "[" + me.nickname + "]님이 ${partner}님을 채팅방에 초대하였습니다.",
                notificationType = NotificationType.CHAT,
                url = "/chat-room/" + chatRoom.id

            ).apply {
                memberId = partnerId
            }

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
            return ChatRoomRequestDTO(
                chatRoomId = chatRoom.id,
                memberId = me.memberId, nickName = me.nickname,
                partnerName = partnerName,
                createdAt = chatRoom.createdAt
            )
        } catch (e: RedisConnectionFailureException) {
            throw RedisConnectionFailureException("레디스 연결문제 ${e.message}")
        } catch (e: RedisCommandTimeoutException) {
            throw RedisCommandTimeoutException("레디스 서버 과부하 ${e.message}")
        } catch (e: DeserializationException) {
            throw DeserializationException("역직렬화 문제 ${e.message}")
        } catch (e: OutOfMemoryError) {
            throw OutOfMemoryError("기본 명령실패 ${e.message}")
        } catch (e: TransactionException) {
            throw TransactionException("트랜젝션 사용으로 인한 문제 ${e.message}")
        } catch (e: Exception) {
            throw Exception("채팅룸 만들기 실패 ${e.message}")
        }
    }

    // 채팅방 삭제
    fun deleteChatRoom(chatRoomId: Long): Boolean {
        try {
            val currentId = redisTemplate.opsForValue().get("chatRoomRedisId") ?: "0"
            // 너무 큰 숫자가 들어온 경우
            log.info("currntId ${currentId}")
            if (chatRoomId < 0 || currentId.toString().toInt() < chatRoomId) return false
            if (currentId.toString().first().toString().toInt() * 10 >= chatRoomId) {
                chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomException.NOT_FOUND::get)
                chatRoomRepository.deleteById(chatRoomId)
                return true
            } else {
                val foundChatRoom =
                    chatRoomRedisRepository.findById(chatRoomId).orElseThrow(ChatRoomException.NOT_FOUND::get)
                chatRoomRedisRepository.delete(foundChatRoom)
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

            val redisChatRooms = getChatRoomsByNickName(me.nickname)
            log.info("redisChatRooms = $redisChatRooms")
            val dbChatRooms: List<ChatRoomRequestDTO> = chatRoomRepository.findChatRoomByMember(me.memberId)

            // 새로운 List 생성 -> dbchatRoom list에 넣기
            val chatRoomDTOs: MutableList<ChatRoomRequestDTO> = mutableListOf()
            chatRoomDTOs.addAll(dbChatRooms)

            // redisChatRoom -> dto로 변환 후 삽입
            for (chatRooms in redisChatRooms) {
                val chatRoomRequest = objectMapper.convertValue(
                    chatRooms,
                    ChatRoomRedis::class.java
                )
                log.info("chatRoomRequest = $chatRoomRequest")
                val chatRoomRequestDTO = ChatRoomRequestDTO(
                    chatRoomId = chatRoomRequest.id,
                    memberId = chatRoomRequest.member.memberId,
                    nickName = chatRoomRequest.member.nickname,
                    partnerName = chatRoomRequest.partner.nickname,
                    createdAt = chatRoomRequest.createdAt
                )
                log.info("chatRoomRequestDTO = $chatRoomRequestDTO")
                chatRoomDTOs.add(chatRoomRequestDTO)
            }

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
    fun getChatRoomsByNickName(nickName:String):  List<ChatRoomRedis> {
        return chatRoomRedisRepository.findByMemberNickname(nickName)
    }
}

