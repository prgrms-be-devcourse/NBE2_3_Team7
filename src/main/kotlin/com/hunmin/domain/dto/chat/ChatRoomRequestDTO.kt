package com.hunmin.domain.dto.chat

import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class ChatRoomRequestDTO (
    @NotNull
    var chatRoomId: Long? = null,
    @NotNull
    var memberId: Long? = null,
    @NotNull
    var nickName: String? = null,
    var partnerName: String? = null,
    @NotNull
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
