package com.hunmin.domain.exception.follow

class FollowTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
