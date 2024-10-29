package com.hunmin.domain.dto.chat

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ChatRoomRequestDTO (
    @NotNull( message = " 채팅방 아이디는 필수입니다.")
    var chatRoomId: Long? = null,
    @NotNull( message = " 사용자 아이디는 필수입니다.")
    var memberId: Long? = null,
    @NotNull( message = " 사용자 닉네임은 필수입니다.")
    var nickName: String? = null,
    var partnerName: String? = null,
    var createdAt: LocalDateTime? = null,
){
    constructor(chatRoomRequestDTO: ChatRoomRequestDTO): this(
        chatRoomRequestDTO.chatRoomId,
        chatRoomRequestDTO.memberId,
        chatRoomRequestDTO.nickName,
        chatRoomRequestDTO.partnerName,
        chatRoomRequestDTO.createdAt
    )
}
