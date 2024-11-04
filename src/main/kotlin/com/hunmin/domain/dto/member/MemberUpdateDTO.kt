package com.hunmin.domain.dto.member

data class MemberUpdateDTO (
    val nickname: String? = null,
    val password: String? = null,
    val country: String? = null,
    val level: String? = null,
    var image: String? = null
)