package com.hunmin.domain.dto.notification

import com.hunmin.domain.entity.NotificationType

data class NotificationSendDTO(
    val notificationId: Long,
    val memberId: Long? = null,
    val message: String,
    val notificationType: NotificationType,
    val url: String
)