package com.hunmin.domain.dto.chat

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

import java.time.LocalDateTime

data class ChatMessageRequestDTO (
    @NotNull( message = " 채팅방 아이디는 필수입니다.")
    var chatRoomId: Long? = null,
    @NotNull( message = " 사용자 아이디는 필수입니다.")
    var memberId: Long? = null,
    @NotBlank( message = " 사용자 닉네임은 필수입니다.")
    var nickName: String? = null,

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
