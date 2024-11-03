package com.hunmin.domain.redis.entity

import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.entity.MessageType
import jakarta.persistence.Id
import org.springframework.data.redis.core.RedisHash
import java.time.LocalDateTime

@RedisHash("chat_message_redis")
data class ChatMessageRedis (
    @Id
    var id: Long,
    val chatRoom: ChatRoomRedis,
    val member: MemberDTO,
    var message: String? =  null,
    var type: MessageType = MessageType.TALK,
    var createdAt : LocalDateTime = LocalDateTime.now(),
    var updatedAt : LocalDateTime? = null
)