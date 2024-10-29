package com.hunmin.domain.exception

class BoardTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)
