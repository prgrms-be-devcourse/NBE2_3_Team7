package com.hunmin.domain.exception.chat

enum class ChatMessageException(val message: String, val code: Int) {
    NOT_FOUND("NOT FOUND CHAT_MESSAGES", 400),
    MESSAGE_NOT_REGISTERED("CHAT_MESSAGES Not Registered", 400),
    NOT_FETCHED("CHAT_MESSAGES_LIST_PAGE NOT FETCHED", 400);

    fun get(): ChatMessageTaskException {
        return ChatMessageTaskException(message,code)
    }
}
