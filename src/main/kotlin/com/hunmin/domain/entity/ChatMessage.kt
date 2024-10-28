package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val chatMessageId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    var chatRoom: ChatRoom? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @Column(name = "message", nullable = false, length = 255)
    var message: String? = null,

    @Enumerated(EnumType.STRING)
    var type: MessageType? = null

) : BaseTimeEntity() {

    override fun hashCode(): Int {
        return chatMessageId.hashCode()
    }

    override fun toString(): String {
        return "chatMessageId = $chatMessageId, message = $message, type = $type"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatMessage) return false
        return chatMessageId == other.chatMessageId
    }

    fun setChatRoom(chatRoom: ChatRoom) {
        this.chatRoom = chatRoom
    }
}