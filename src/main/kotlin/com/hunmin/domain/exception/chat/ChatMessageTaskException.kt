package com.hunmin.domain.exception.chat

class ChatMessageTaskException(message: String, val code: Int) : RuntimeException(message)
