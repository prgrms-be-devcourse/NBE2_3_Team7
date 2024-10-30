package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class Notification (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val notificationId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @Column(nullable = false)
    var message: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var notificationType: NotificationType,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = false)
    var url: String
) : BaseTimeEntity() {
    class Builder {
        private var notificationId: Long = 0
        private var member: Member? = null
        private lateinit var message: String
        private lateinit var notificationType: NotificationType
        private var isRead: Boolean = false
        private lateinit var url: String

        fun notificationId(notificationId: Long) = apply { this.notificationId = notificationId }
        fun member(member: Member) = apply { this.member = member }
        fun message(message: String) = apply { this.message = message}
        fun notificationType(notificationType: NotificationType) = apply { this.notificationType = notificationType }
        fun isRead(isRead: Boolean) = apply { this.isRead = isRead }
        fun url(url: String) = apply { this.url = url }

        fun build() = Notification(
            notificationId = notificationId,
            member = member,
            message = message,
            notificationType = notificationType,
            isRead = isRead,
            url = url
        )
    }

    companion object {
        fun builder() = Builder()
    }

    fun changeIsRead(isRead: Boolean) {
        this.isRead = isRead
    }

    override fun hashCode(): Int {
        return notificationId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notification) return false
        return notificationId == other.notificationId
    }

    override fun toString(): String {
        return "Notification(notificationId=$notificationId, message='$message', notificationType=$notificationType, url='$url')"
    }
}