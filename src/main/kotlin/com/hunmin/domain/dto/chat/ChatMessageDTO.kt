package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.MessageType
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class ChatMessageDTO (
    @NotNull
    var chatMessageId: Long? = null,
    @NotNull
    var chatRoomId: Long? = null,
    @NotNull
    var memberId: Long? = null,

    @NotBlank
    var nickName: String? = null,
    var message: String? = null,
    var type: MessageType? = null,
    @NotNull
    var createdAt: LocalDateTime? = null
){
    constructor(chatMessageDTO: ChatMessageDTO): this(chatMessageDTO.chatMessageId,
        chatMessageDTO.chatRoomId,
        chatMessageDTO.memberId,
        chatMessageDTO.nickName,
        chatMessageDTO.message,
        chatMessageDTO.type,
        chatMessageDTO.createdAt)
}
