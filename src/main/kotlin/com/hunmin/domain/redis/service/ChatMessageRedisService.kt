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
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.NotificationService
import mu.KotlinLogging
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class ChatMessageRedisService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val memberRepository: MemberRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val redisSubscriber: RedisSubscriber,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters,
    private val chatRoomRedisRepository: ChatRoomRedisRepository,
    private val chatMessageRedisRepository: ChatMessageRedisRepository
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

                // 알림
                val senderId = sender.memberId
                val receiverId = chatRoom.partner.memberId

                if (receiverId == null) {
                    throw NoSuchElementException("수신자가 등록되지 않았습니다.")
                }

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
                            emitter.send(ChatMessageDTO(chatMessage))
                        } catch (e: IOException) {
                            log.error("Error sending comment to client via SSE: ${e.message}")
                            sseEmitters.delete(emitterId)
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

                // 알림
                val senderId = sender.memberId
                val receiverId = redisChatRoom.partner.memberId

                if (receiverId == null) {
                    throw NoSuchElementException("수신자가 등록되지 않았습니다.")
                }

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

        } catch (e: Exception) {
            log.error("채팅 메세지 전송에 실패하였습니다. $e.message")
            throw ChatRoomException.FAILED_REGISTER.get()
        }
    }

    // 모든 채팅 기록 조회
    fun readAllMessages(chatRoomId: Long): List<ChatMessageDTO> {
        try {

            val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow()

            val chatLists = chatRoom.chatMessage?.map { chatMessage ->
                ChatMessageDTO(chatMessage)
            }?.toList() ?: emptyList()

            return chatLists
        } catch (e: RuntimeException) {
            log.error("모든 채팅기록 불러오는데 실패했습니다. $e.message")
            throw ChatMessageException.NOT_FOUND.get()
        }
    }

    //채팅 조회
    fun readChatMessage(chatMessageId: Long): ChatMessageDTO {
        return ChatMessageDTO(
            chatMessageRepository.findById(chatMessageId).orElse(null)
                ?: throw ChatMessageException.NOT_FOUND.get()
        )
    }

    //채팅 수정
    fun updateChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessageDTO {
        val foundChatMessage: ChatMessage = chatMessageRepository.findById(chatMessageDTO.chatMessageId).orElse(null)
            ?: throw ChatMessageException.NOT_FOUND.get()

        foundChatMessage.message = chatMessageDTO.message
        return ChatMessageDTO(chatMessageRepository.save(foundChatMessage))
    }

    //채팅 삭제
    fun deleteChatMessage(chatMessageId: Long): Boolean {
        val chatMessage: ChatMessage = chatMessageRepository.findById(chatMessageId)
            .orElse(null) ?: throw ChatMessageException.NOT_FOUND.get()
        chatMessageRepository.deleteById(chatMessageId)
        return true
    }

    //채팅목록 페이징
    fun getList(pageRequestDTO: PageRequestDTO, chatRoomId: Long): Page<ChatMessageListRequestDTO> { //목록
        try {
            val sort = Sort.by("createdAt").descending()
            val pageable: Pageable = pageRequestDTO.getPageable(sort)
            val result = chatMessageRepository.chatMessageList(pageable, chatRoomId)
            logger.info("=== 페이징 결과 {${result.toList()}}")
            return result
        } catch (e: Exception) {
            log.error("쳇서비스 페이징 실패 $e.message")
            throw ChatMessageException.NOT_FETCHED.get()
        }
    }
}