package com.hunmin.domain.exception

data class MemberTaskException(
    override val message: String,
    val code: Int
) : RuntimeException(message)