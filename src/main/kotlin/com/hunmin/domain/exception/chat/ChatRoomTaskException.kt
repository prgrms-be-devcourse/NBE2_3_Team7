package com.hunmin.domain.exception.chat


class ChatRoomTaskException(message: String, val code: Int) : RuntimeException(message)
