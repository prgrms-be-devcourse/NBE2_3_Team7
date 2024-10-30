package com.hunmin.domain.exception

enum class BoardException(val boardTaskException: BoardTaskException) {
    NOT_FOUND("BOARD NOT_FOUND", 404),
    NOT_CREATED("BOARD NOT_CREATED", 400),
    NOT_UPDATED("BOARD NOT_UPDATED", 400),
    NOT_DELETED("BOARD NOT_DELETED", 400);

    constructor(message: String, code: Int) : this(BoardTaskException(message, code))

    fun toException(): BoardTaskException {
        return this.boardTaskException
    }
}
