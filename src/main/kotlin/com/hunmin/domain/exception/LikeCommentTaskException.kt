package com.hunmin.domain.exception

class LikeCommentTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
