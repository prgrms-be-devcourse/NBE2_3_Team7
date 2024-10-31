package com.hunmin.domain.exception.chat

class ChatMessageTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
