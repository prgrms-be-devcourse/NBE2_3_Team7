package com.hunmin.domain.exception

class WordTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)