package com.hunmin.domain.exception


class AdminTaskException(
    override val message: String ,
    val code: Int
) : RuntimeException(message)