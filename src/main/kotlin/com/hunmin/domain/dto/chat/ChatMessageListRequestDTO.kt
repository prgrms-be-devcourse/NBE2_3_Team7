package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.MessageType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ChatMessageListRequestDTO(
    @NotNull( message = " 채팅 아이디는 필수입니다.")
    var chatMessageId: Long? = null,
    @NotNull( message = " 사용자 아이디는 필수입니다.")
    var memberId: Long? = null,

    var message: String? = null,

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
