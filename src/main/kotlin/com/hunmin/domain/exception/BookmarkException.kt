package com.hunmin.domain.exception

enum class BookmarkException(val bookmarkTaskException: BookmarkTaskException) {
    NOT_FOUND("BOOKMARK NOT_FOUND", 404),
    NOT_CREATED("BOOKMARK NOT_CREATED", 400),
    NOT_DELETED("BOOKMARK NOT_DELETED", 400);

    constructor(message: String, code: Int) : this(BookmarkTaskException(message, code))

    fun toException(): BookmarkTaskException {
        return this.bookmarkTaskException
    }
}
