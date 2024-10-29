package com.hunmin.domain.dto.chat

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class ChatMessageRequestDTO (
    @NotNull
    var chatRoomId: Long? = null,
    @NotNull
    var memberId: Long? = null,
    @NotBlank
    var nickName: String? = null,

    @NotNull
    var createdAt: LocalDateTime? = null,
    var userCount: Long? = null,
){
    constructor(chatMessageRequestDTO: ChatMessageRequestDTO): this(
        chatMessageRequestDTO.chatRoomId,
        chatMessageRequestDTO.memberId,
        chatMessageRequestDTO.nickName,
        chatMessageRequestDTO.createdAt,
        chatMessageRequestDTO.userCount,
        )
}
