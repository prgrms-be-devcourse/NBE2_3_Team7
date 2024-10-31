package com.hunmin.domain.exception.chat


class ChatRoomTaskException(override val message: String, val code: Int) : RuntimeException(message)
