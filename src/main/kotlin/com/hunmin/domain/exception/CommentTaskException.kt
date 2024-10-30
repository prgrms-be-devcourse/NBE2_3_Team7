package com.hunmin.domain.exception

class CommentTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
