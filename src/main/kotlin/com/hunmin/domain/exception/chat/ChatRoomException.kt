package com.hunmin.domain.exception.chat

enum class ChatRoomException(val message: String,val code: Int) {
    CHATROOM_ALREADY_EXIST("CHATROOM EXISTS", 400),
    NOT_FOUND("NOT FOUND CHAT_ROOM", 400),
    FAILED_REGISTER("FAILED REGISTER", 400),
    FAILED_READ_ROOMS("FAILED READ ROOMS", 400),
    CHATROOM_NOT_REGISTERED("CHATROOM NOT REGISTERED", 400);


    fun get(): ChatRoomTaskException {
        return ChatRoomTaskException(message, code)
    }
}
