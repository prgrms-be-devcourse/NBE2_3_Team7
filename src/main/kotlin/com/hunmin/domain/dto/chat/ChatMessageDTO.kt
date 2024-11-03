package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.ChatMessage
import com.hunmin.domain.entity.MessageType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ChatMessageDTO(
    @NotNull(message = " 채팅 아이디는 필수입니다.")
    val chatMessageId: Long,
    @NotNull(message = " 채팅방 아이디는 필수입니다.")
    val chatRoomId: Long,
    @NotNull(message = " 사용자 아이디는 필수입니다.")
    val memberId: Long,

    @NotBlank(message = " 사용자 닉네임은 필수입니다.")
    val nickName: String,
    var message: String? = null,
    var type: MessageType? = null,
    val createdAt: LocalDateTime? = null
) {
    constructor(chatMessage: ChatMessage) : this(
        chatMessage.chatMessageId,
        chatMessage.chatRoom.chatRoomId,
        memberId = chatMessage.member.memberId,
        nickName = chatMessage.member.nickname,
        chatMessage.message,
        chatMessage.type,
        chatMessage.createdAt
    )
}
