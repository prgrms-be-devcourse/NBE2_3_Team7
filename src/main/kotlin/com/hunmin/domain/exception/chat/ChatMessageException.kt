package com.hunmin.domain.exception.chat

enum class ChatMessageException(val chatMessageTaskException: ChatMessageTaskException) {
    NOT_FOUND("NOT FOUND CHAT_MESSAGES", 400),
    NOT_FETCHED("CHAT_MESSAGES_LIST_PAGE NOT FETCHED", 400);

    constructor(message: String, code: Int) : this(ChatMessageTaskException(message, code))

    fun get(): ChatMessageTaskException {
        return this.chatMessageTaskException
    }
}
