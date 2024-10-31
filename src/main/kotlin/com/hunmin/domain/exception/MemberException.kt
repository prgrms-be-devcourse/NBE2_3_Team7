package com.hunmin.domain.exception

enum class MemberException(
    private val message: String,
    private val code: Int
) {
    NOT_FOUND("NOT_FOUND", 404),
    DUPLICATE("DUPLICATE", 409),
    INVALID("INVALID", 400),
    BAD_CREDENTIALS("BAD_CREDENTIALS", 401);

    private val memberTaskException: MemberTaskException by lazy {
        MemberTaskException(message, code)
    }

    fun get(): MemberTaskException = memberTaskException
}
