package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.ChatRoom
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ChatRoomDTO(
    @NotNull( message = " 채팅방 아이디는 필수입니다.")
    val chatRoomId: Long?,
    @NotNull( message = " 사용자 아이디는 필수입니다.")
    val memberId: Long,
    @NotNull( message = " 사용자 닉네임은 필수입니다.")
    val nickName: String,

    val userCount: Long? = null,
    val chatMessageIds: MutableList<Long>? = mutableListOf(),
    val latestMessageContent: String? = null,
    val latestMessageDate: LocalDateTime? = null
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
