package com.hunmin.domain.exception.follow

class FollowTaskException(
    message: String,
    val code: Int
) : RuntimeException(message)
