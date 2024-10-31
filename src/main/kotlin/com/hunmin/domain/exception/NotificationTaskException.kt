package com.hunmin.domain.exception

class NotificationTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
