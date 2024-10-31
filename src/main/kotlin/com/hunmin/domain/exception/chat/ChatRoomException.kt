package com.hunmin.domain.exception.chat

enum class ChatRoomException(val chatRoomTaskException: ChatRoomTaskException) {
    CHATROOM_ALREADY_EXIST("CHATROOM EXISTS", 400),
    NOT_FOUND("NOT FOUND CHAT_ROOM", 400),
    FAILED_REGISTER("FAILED REGISTER", 400),
    FAILED_READ_ROOMS("FAILED READ ROOMS", 400);

    constructor(message: String, code: Int) : this(ChatRoomTaskException(message, code))

    fun get(): ChatRoomTaskException {
        return this.chatRoomTaskException
    }
}
