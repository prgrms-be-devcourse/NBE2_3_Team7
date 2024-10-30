package com.hunmin.domain.exception

enum class CommentException(val commentTaskException: CommentTaskException) {
    NOT_FOUND("BOARD NOT_FOUND", 404),
    NOT_CREATED("BOARD NOT_CREATED", 400),
    NOT_UPDATED("BOARD NOT_UPDATED", 400),
    NOT_DELETED("BOARD NOT_DELETED", 400);

    constructor(message: String, code: Int) : this(CommentTaskException(message, code))

    fun toException(): CommentTaskException {
        return this.commentTaskException
    }
}
