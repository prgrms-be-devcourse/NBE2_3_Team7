package com.hunmin.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
data class ChatRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val chatRoomId: Long = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    var member: Member,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partner_id")
    var partner: Member,

    @BatchSize(size = 50)
    @OneToMany(
        mappedBy = "chatRoom",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    var chatMessage: MutableList<ChatMessage>? = null,

    var userCount: Long = 1

) : BaseTimeEntity() {

    override fun hashCode(): Int {
        return chatRoomId.hashCode()
    }
    override fun toString(): String {
        return "chatRoomId = $chatRoomId, userCount = $userCount,"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatRoom) return false
        return chatRoomId == other.chatRoomId
    }
}
