package com.hunmin.domain.redis.service

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.dto.chat.ChatMessageListRequestDTO
import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.ChatMessage
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.chat.ChatMessageException
import com.hunmin.domain.exception.chat.ChatRoomException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.redis.entity.ChatMessageRedis
import com.hunmin.domain.redis.repository.ChatMessageRedisRepository
import com.hunmin.domain.redis.repository.ChatRoomRedisRepository
import com.hunmin.domain.redis.sendMessage.RedisSubscriber
import com.hunmin.domain.repository.ChatMessageRepository
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.NotificationService
import mu.KotlinLogging
import org.hibernate.query.sqm.tree.SqmNode.log
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class ChatMessageRedisService(
    private val modelMapper: ModelMapper,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val memberRepository: MemberRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val redisSubscriber: RedisSubscriber,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters,
    private val chatRoomRedisRepository: ChatRoomRedisRepository,
    private val chatMessageRedisRepository: ChatMessageRedisRepository,
    private val followRepository: FollowRepository,
    private val chatRoomRedisService: ChatRoomRedisService
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    // 채팅방에 메시지 발송
    fun sendChatMessage(chatMessageDTO: ChatMessageDTO) {
        try {
            log.info("chatMessageDTO받음_메시지 $chatMessageDTO")

            val redisChatRoom = chatRoomRedisRepository.findById(chatMessageDTO.chatRoomId).getOrNull()
            val sender: Member = memberRepository.findById(chatMessageDTO.memberId).get()

            //DB에 챗룸이 있다면
            if (redisChatRoom == null) {
                val chatRoom =
                    chatRoomRepository.findById(chatMessageDTO.chatRoomId).orElseThrow(ChatRoomException.NOT_FOUND::get)

                val chatMessage = ChatMessage(chatRoom = chatRoom, member = sender).apply {
                    message = chatMessageDTO.message
                    type = chatMessageDTO.type
                }
                val savedChatMessage: ChatMessage = chatMessageRepository.save(chatMessage)
                log.info("savedChatMessage받음_메시지 $savedChatMessage")
                redisSubscriber.sendMessage(ChatMessageDTO(savedChatMessage))

                // 알림 (팔로우 상태만 받기)
                val senderId = sender.memberId
                val receiverId = chatRoom.partner.memberId

                if (receiverId == null) {
                    throw NoSuchElementException("수신자가 등록되지 않았습니다.")
                }
                // 팔로우 차단이 아닌 경우에만 메세지 받기
                val foundFollow = followRepository.findByMemberId(receiverId, senderId)
                if (foundFollow.isPresent) {
                    if (!foundFollow.get().isBlock && foundFollow.get().notification) {
                        val notificationSendDTO: NotificationSendDTO = NotificationSendDTO(
                            message = sender.nickname + "님 : " + chatMessageDTO.message,
                            notificationType = NotificationType.CHAT,
                            url = "/chat-room/" + chatMessageDTO.chatRoomId
                        ).apply {
                            this.memberId = receiverId
                        }
                        notificationService.send(notificationSendDTO)

                        val emitterId = receiverId.toString() + "_"
                        val emitter = sseEmitters.findSingleEmitter(emitterId)

                        if (emitter != null) {
                            try {
                                emitter.send(ChatMessageDTO(chatMessage))
                            } catch (e: IOException) {
                                log.error("Error sending comment to client via SSE: ${e.message}")
                                sseEmitters.delete(emitterId)
                            }
                        }
                    }
                }
            } else {
                // redis에 챗룸이 있다면
                val memberDTO = MemberDTO(
                    memberId = sender.memberId, email = sender.email,
                    nickname = sender.nickname,
                    level = sender.level,
                    password = sender.password,
                    country = sender.country,
                    image = sender.image
                )
                val increasedId = redisTemplate.opsForValue().increment("chatMessageRedisId")!!
                val chatMessage = ChatMessageRedis(
                    id = increasedId,
                    chatRoom = redisChatRoom,
                    member = memberDTO,
                    message = chatMessageDTO.message,
                    type = chatMessageDTO.type
                )
                val savedChatMessage = chatMessageRedisRepository.save(chatMessage)
                log.info("savedChatMessage받음_메시지 $savedChatMessage")
                val newChatMessageDTO = chatMessageDTO.copy(
                    createdAt = chatMessage.createdAt,
                    chatMessageId = chatMessage.id
                )
                redisSubscriber.sendMessage(newChatMessageDTO)

                // 알림 (팔로우 상태만 받기)
                val senderId = sender.memberId
                val receiverId = redisChatRoom.partner.memberId

                if (receiverId == null) {
                    throw NoSuchElementException("수신자가 등록되지 않았습니다.")
                }
                // 팔로우 차단이 아닌 경우에만 메세지 받기
                val foundFollow = followRepository.findByMemberId(receiverId, senderId)
                if (foundFollow.isPresent) {
                    if (!foundFollow.get().isBlock && foundFollow.get().notification) {
                        if (receiverId != senderId) {
                            val notificationSendDTO: NotificationSendDTO = NotificationSendDTO(
                                message = sender.nickname + "님 : " + chatMessageDTO.message,
                                notificationType = NotificationType.CHAT,
                                url = "/chat-room/" + chatMessageDTO.chatRoomId
                            ).apply {
                                this.memberId = receiverId
                            }
                            notificationService.send(notificationSendDTO)

                            val emitterId = receiverId.toString() + "_"
                            val emitter = sseEmitters.findSingleEmitter(emitterId)

                            if (emitter != null) {
                                try {
                                    emitter.send(newChatMessageDTO)
                                } catch (e: IOException) {
                                    log.error("Error sending comment to client via SSE: ${e.message}")
                                    sseEmitters.delete(emitterId)
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            log.error("채팅 메세지 전송에 실패하였습니다. $e.message")
            throw ChatRoomException.FAILED_REGISTER.get()
        }
    }

    //채팅 수정
    fun updateChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessageDTO {
        val chatMessageRedis = chatMessageRedisRepository.findById(chatMessageDTO.chatMessageId)

        log.info("chatMessageDTO는? $chatMessageDTO")
        log.info("chatMessageRedis는? $chatMessageRedis")
        if (chatMessageRedis.isEmpty) {
            val foundChatMessage =
                chatMessageRepository.findById(chatMessageDTO.chatMessageId).orElse(null)
                    ?: throw ChatMessageException.NOT_FOUND.get()
            log.info("foundChatMessage는? $foundChatMessage")
            foundChatMessage.message = chatMessageDTO.message
            return ChatMessageDTO(chatMessageRepository.save(foundChatMessage))
        } else {
            val updatedChatMessage = chatMessageRedis.get().copy(
                message = chatMessageDTO.message,
                updatedAt = LocalDateTime.now()
            )
            log.info("updatedChatMessage는? $updatedChatMessage")
            chatMessageRedisRepository.save(updatedChatMessage)
            return ChatMessageDTO(
                chatMessageId = updatedChatMessage.id,
                chatRoomId = updatedChatMessage.chatRoom.id,
                memberId = updatedChatMessage.member.memberId,
                nickName = updatedChatMessage.member.nickname,
                message = updatedChatMessage.message,
                createdAt = updatedChatMessage.createdAt,
                type = updatedChatMessage.type
            )
        }
    }

    //채팅 삭제
    fun deleteChatMessage(chatMessageId: Long): Boolean {
        try {
            val redisChatMessage = chatMessageRedisRepository.findById(chatMessageId)
            if (redisChatMessage.isEmpty) {
                val chatMessage = chatMessageRepository.findById(chatMessageId)
                if (chatMessage.isEmpty) return false
                chatMessageRepository.deleteById(chatMessageId)
                return true
            } else {
                chatMessageRedisRepository.deleteById(chatMessageId)
                return true
            }
        }catch (e:Exception){
            log.error("채팅 삭제에 실패하였습니다. ${e.message}")
            throw e
        }
    }

    //채팅목록 페이징
    fun getList(pageRequestDTO: PageRequestDTO, chatRoomId: Long): Page<ChatMessageListRequestDTO> { //목록
        try {
            // 모든 메세지를 담을 리스트 생성
            var chatMessageListDTOs = mutableListOf<ChatMessageListRequestDTO>()

            // DB 채팅 메세지 삽입
            val foundChatMessages = chatMessageRepository.getChatMessageList(chatRoomId)
            chatMessageListDTOs.addAll(foundChatMessages)

            // 레디스 채팅 메세지 삽입
            val foundRedisChatMessages: List<ChatMessageRedis> = chatMessageRedisRepository.findAll().toMutableList()
            log.info("foundRedisChatRoom_확인 $foundRedisChatMessages")
            if (foundRedisChatMessages.isNotEmpty()) {
                for (redisMessage in foundRedisChatMessages) {
                    if (redisMessage.chatRoom.id == chatRoomId) {
                        val newChatMessageDTO = ChatMessageListRequestDTO(
                            chatMessageId = redisMessage.id,
                            memberId = redisMessage.member.memberId,
                            message = redisMessage.message,
                            createdAt = redisMessage.createdAt,
                            type = redisMessage.type
                        )
                        log.info("newChatMessageDTO_확인 $newChatMessageDTO")
                        chatMessageListDTOs.add(newChatMessageDTO)
                        log.info("chatMessageListDTOs_확인 $chatMessageListDTOs")
                    }
                }
            }
            log.info("=== 모든 리스트 결과 {${chatMessageListDTOs}}")

            // 페이지네이션 처리를 위해 전체 리스트 정렬
            chatMessageListDTOs.sortBy { it.chatMessageId }

            //pageRequestDTO 페이지네이션 진행
            val pageable = pageRequestDTO.getPageable(Sort.by("chatMessageId").descending())
            val start = pageable.offset.toInt()
            val end = (start + pageable.pageSize).coerceAtMost(chatMessageListDTOs.size)
            val pagedChatMessageListDTOs = chatMessageListDTOs.subList(start, end)

            return PageImpl(pagedChatMessageListDTOs, pageable, chatMessageListDTOs.size.toLong())
        } catch (e: Exception) {
            log.error("쳇서비스 페이징 실패 $e.message")
            throw ChatMessageException.NOT_FETCHED.get()
        }
    }
}