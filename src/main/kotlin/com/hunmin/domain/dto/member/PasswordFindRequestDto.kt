package com.hunmin.domain.dto.member

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class PasswordFindRequestDto (
    @field:Email
    val email: String,

    @field:NotBlank
    val nickname: String
)
