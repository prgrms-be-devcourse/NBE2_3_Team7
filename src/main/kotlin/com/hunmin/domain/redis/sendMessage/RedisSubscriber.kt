package com.hunmin.domain.redis.sendMessage

import com.fasterxml.jackson.databind.ObjectMapper
import com.hunmin.domain.dto.chat.ChatMessageDTO
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.lang.Nullable
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service

@Service
class RedisSubscriber(
    val objectMapper: ObjectMapper,
    val messagingTemplate: SimpMessageSendingOperations,
    val redisTemplate: RedisTemplate<*, *>
) : MessageListener {

    //메세지를 구독자들에게 송신
    fun sendMessage(publishMessage: ChatMessageDTO) {
        try {
            messagingTemplate.convertAndSend("/sub/chat/room/${publishMessage.chatRoomId}", publishMessage)
        } catch (e: Exception) {
            log.error("Exception {}", e)
        }
    }

    //stomp이용해서 구독자들에게 송신
    override fun onMessage(message: Message, @Nullable pattern: ByteArray?) {
        try {
            val publishMessage = redisTemplate.stringSerializer.deserialize(message.body) as String

            val chatMessage = objectMapper.readValue(publishMessage, ChatMessageDTO::class.java)

            messagingTemplate.convertAndSend("/sub/chat/room/${chatMessage.chatRoomId}", chatMessage)
        } catch (e: Exception) {
            log.error(e.message)
        }
    }
}
