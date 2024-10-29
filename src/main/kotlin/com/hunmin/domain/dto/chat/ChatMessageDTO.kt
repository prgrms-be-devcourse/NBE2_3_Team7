package com.hunmin.domain.dto.chat

import com.hunmin.domain.entity.MessageType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ChatMessageDTO (
    @NotNull( message = " 채팅 아이디는 필수입니다.")
    val chatMessageId: Long? = null,
    @NotNull( message = " 채팅방 아이디는 필수입니다.")
    val chatRoomId: Long? = null,
    @NotNull( message = " 사용자 아이디는 필수입니다.")
    val memberId: Long? = null,

    @NotBlank( message = " 사용자 닉네임은 필수입니다.")
    var nickName: String? = null,
    var message: String? = null,
    var type: MessageType? = null,
    @NotNull( message = " 채팅 사용날짜는 필수 입니다.")
    var createdAt: LocalDateTime? = null
){
    constructor(chatMessageDTO: ChatMessageDTO): this(chatMessageDTO.chatMessageId,
        chatMessageDTO.chatRoomId,
        chatMessageDTO.memberId,
        chatMessageDTO.nickName,
        chatMessageDTO.message,
        chatMessageDTO.type,
        chatMessageDTO.createdAt)
}
