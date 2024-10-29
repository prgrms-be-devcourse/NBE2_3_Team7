package com.hunmin.domain.dto.chat

import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class ChatRoomDTO(
    @NotNull
    var chatRoomId: Long? = null,
    @NotNull
    var memberId: Long? = null,
    @NotNull
    var nickName: String? = null,

    var userCount: Long? = null,
    var chatMessageIds: MutableList<Long>? = mutableListOf(),
    var latestMessageContent: String? = null,
    var latestMessageDate: LocalDateTime? = null
){
    constructor(chatRoomDTO: ChatRoomDTO): this(
        chatRoomDTO.chatRoomId,
        chatRoomDTO.memberId,
        chatRoomDTO.nickName,
        chatRoomDTO.userCount,
        chatRoomDTO.chatMessageIds,
        chatRoomDTO.latestMessageContent,
        chatRoomDTO.latestMessageDate)
}
