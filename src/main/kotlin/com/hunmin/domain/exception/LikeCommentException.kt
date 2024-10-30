package com.hunmin.domain.exception

enum class LikeCommentException(val likeCommentTaskException: LikeCommentTaskException) {
    NOT_FOUND("LIKECOMMENT NOT_FOUND", 404),
    NOT_CREATED("LIKECOMMENT NOT_CREATED", 400),
    NOT_DELETED("LIKECOMMENT NOT_DELETED", 400);

    constructor(message: String, code: Int) : this(LikeCommentTaskException(message, code))

    fun toException(): LikeCommentTaskException {
        return this.likeCommentTaskException
    }
}
