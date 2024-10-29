package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.MessageType
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class ChatMessageListRequestDTO(
    @NotNull
    var chatMessageId: Long? = null,
    @NotNull
    var memberId: Long? = null,
    @NotBlank
    var message: String? = null,
    @NotNull
    var createdAt: LocalDateTime? = null,
    var type: MessageType? = null,
) {
    constructor(chatMessageListRequestDTO: ChatMessageListRequestDTO) : this(
        chatMessageListRequestDTO.chatMessageId,
        chatMessageListRequestDTO.memberId,
        chatMessageListRequestDTO.message,
        chatMessageListRequestDTO.createdAt,
        chatMessageListRequestDTO.type
    )
}
