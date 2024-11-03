package com.hunmin.domain.redis.entity

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.entity.ChatMessage
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.redis.core.RedisHash
import java.time.LocalDateTime

@RedisHash("chat_room_redis")
data class ChatRoomRedis (
    @Id
    var id: Long,
    val member: MemberDTO,
    val partner: MemberDTO,
    var chatMessage: MutableList<ChatMessageRedis>? =  mutableListOf(),
    var userCount: Long = 1,
    var createdAt : LocalDateTime = LocalDateTime.now(),
    var updatedAt : LocalDateTime? = null
)