package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.ChatRoom
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

data class ChatRoomDTO(
    @NotNull
    var chatRoomId: Long?,
    @NotNull
    var memberId: Long,
    @NotNull
    var nickName: String,

    var userCount: Long? = null,
    var chatMessageIds: MutableList<Long>? = mutableListOf(),
    var latestMessageContent: String? = null,
    var latestMessageDate: LocalDateTime? = null
){
    constructor(chatRoom: ChatRoom): this(
        chatRoom.chatRoomId,
        chatRoom.member!!.memberId,
        chatRoom.member!!.nickname,
        chatRoom.userCount,
        chatMessageIds = chatRoom.chatMessage?.map { it.chatMessageId }?.toMutableList(),
        latestMessageContent = chatRoom.chatMessage?.lastOrNull()?.message ?: "No messages",
        latestMessageDate = chatRoom.createdAt
    )
}
