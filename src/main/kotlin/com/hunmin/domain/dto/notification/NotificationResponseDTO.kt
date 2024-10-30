package com.hunmin.domain.dto.notification

import com.hunmin.domain.entity.Notification
import com.hunmin.domain.entity.NotificationType

data class NotificationResponseDTO(
    val notificationId: Long,
    val message: String,
    val notificationType: NotificationType,
    val url: String,
    val isRead: Boolean

) {
    constructor(notification: Notification) : this(
        notificationId = notification.notificationId,
        message = notification.message,
        notificationType = notification.notificationType,
        url = notification.url,
        isRead = notification.isRead
    )
}