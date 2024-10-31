package com.hunmin.domain.exception


class NoticeTaskException(
    override val message: String ,
    val code: Int
) : RuntimeException(message)