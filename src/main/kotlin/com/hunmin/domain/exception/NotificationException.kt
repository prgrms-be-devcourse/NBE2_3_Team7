package com.hunmin.domain.exception

enum class NotificationException(val notificationTaskException: NotificationTaskException) {
    NOT_FOUND("NOTIFICATION NOT_FOUND", 404),
    NOT_SEND("NOTIFICATION NOT_SEND", 400),
    NOT_UPDATED("NOTIFICATION NOT_UPDATED", 400);

    constructor(message: String, code: Int) : this(NotificationTaskException(message, code))

    fun toException(): NotificationTaskException {
        return this.notificationTaskException
    }
}
