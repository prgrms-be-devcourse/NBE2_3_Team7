package com.hunmin.domain.exception

enum class WordException (val wordTaskException: WordTaskException) {
    WORD_NOT_FOUND("WORD NOT_FOUND", 404),
    WORD_NOT_CREATED("WORD NOT_CREATED", 400),
    WORD_NOT_UPDATED("WORD NOT_UPDATED", 400),
    WORD_NOT_DELETED("WORD NOT_DELETED", 400),
    WORD_FORBIDDEN("ACCESS FORBIDDEN", 403);

    constructor(message: String, code: Int) : this(WordTaskException(message, code))

    fun toException(): WordTaskException {
        return this.wordTaskException
    }
}