package com.hunmin.domain.exception

enum class WordException (val wordTaskException: WordTaskException) {
    WORD_NOT_FOUND("단어가 존재하지 않습니다.", 404),
    WORD_NOT_CREATED("단어 생성에 실패하였습니다.", 400),
    WORD_NOT_UPDATED("단어 수정에 실패하였습니다.", 400),
    WORD_NOT_DELETED("단어 삭제에 실패하였습니다.", 400),
    WORD_FORBIDDEN("관리자 권한이 필요합니다.", 403);

    constructor(message: String, code: Int) : this(WordTaskException(message, code))

    fun toException(): WordTaskException {
        return this.wordTaskException
    }
}