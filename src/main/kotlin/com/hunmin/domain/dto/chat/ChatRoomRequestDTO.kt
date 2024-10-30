package com.hunmin.domain.dto.chat

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ChatRoomRequestDTO (
    @NotNull( message = " 채팅방 아이디는 필수입니다.")
    val chatRoomId: Long,
    @NotNull( message = " 사용자 아이디는 필수입니다.")
    val memberId: Long,
    @NotNull( message = " 사용자 닉네임은 필수입니다.")
    val nickName: String,
    var partnerName: String? = null,
    var createdAt: LocalDateTime? = null,
)
