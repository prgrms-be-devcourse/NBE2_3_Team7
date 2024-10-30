package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.MessageType
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ChatMessageListRequestDTO(
    @NotNull( message = " 채팅 아이디는 필수입니다.")
    val chatMessageId: Long,
    @NotNull( message = " 사용자 아이디는 필수입니다.")
    val memberId: Long,

    var message: String? = null,

    var createdAt: LocalDateTime? = null,
    var type: MessageType? = null,
)