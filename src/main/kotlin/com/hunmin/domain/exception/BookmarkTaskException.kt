package com.hunmin.domain.exception

class BookmarkTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
