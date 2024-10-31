package com.hunmin.domain.service

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.dto.chat.ChatMessageListRequestDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.ChatMessage
import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.chat.ChatMessageException
import com.hunmin.domain.exception.chat.ChatRoomException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.pubsub.RedisSubscriber
import com.hunmin.domain.repository.ChatMessageRepository
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.MemberRepository
import jdk.internal.joptsimple.internal.Messages.message
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@Service
@Transactional
class ChatMessageService(
    private val memberRepository: MemberRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val redisSubscriber: RedisSubscriber,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters){

    // 채팅방에 메시지 발송
    fun sendChatMessage(chatMessageDTO: ChatMessageDTO) {
        try {

            val chatRoom: ChatRoom = chatRoomRepository.findById(chatMessageDTO.chatRoomId).get()
            val sender: Member = memberRepository.findById(chatMessageDTO.memberId).get()

            val chatMessage = ChatMessage(chatRoom = chatRoom, member = sender).apply {
                message = chatMessageDTO.message
                type = chatMessageDTO.type!!
            }
            val savedChatMessage: ChatMessage = chatMessageRepository.save(chatMessage)
            redisSubscriber.sendMessage(ChatMessageDTO(savedChatMessage))

            // 알림
            val senderId = sender.memberId
            var receiverId: Long? = null

            val messages: MutableList<ChatMessage> = chatRoom.chatMessage ?: mutableListOf()

            messages.let {
                for (message in it) {
                    if (message.member.memberId != senderId) {
                        receiverId = message.member.memberId
                        break
                    }
                }
            }
            if (receiverId == null) {
                throw NoSuchElementException("수신자가 등록되지 않았습니다.")
            }

            if (receiverId != senderId) {
                val notificationSendDTO: NotificationSendDTO = NotificationSendDTO(
                    message = sender.nickname + "님 : " + chatMessageDTO.message,
                    notificationType = NotificationType.CHAT,
                    url = "/chat-room/" + chatMessageDTO.chatRoomId).apply {
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
        } catch (e: Exception) {
            log.error("채팅 메세지 전송에 실패하였습니다.")
            throw ChatRoomException.FAILED_REGISTER.get()
        }
    }

    // 모든 채팅 기록 조회
    fun readAllMessages(chatRoomId: Long): List<ChatMessageDTO> {
        try {

        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow()

        val chatLists = chatRoom.chatMessage?.map { chatMessage ->
            ChatMessageDTO(chatMessage)
        }?.toList()?:emptyList()

        return chatLists
        }catch (e:RuntimeException){
            log.error("모든 채팅기록 불러오는데 실패했습니다.")
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
            return chatMessageRepository.chatMessageList(pageable, chatRoomId)
        } catch (e: Exception) {
            log.error("쳇서비스 페이징 실패")
            throw ChatMessageException.NOT_FETCHED.get()
        }
    }
}