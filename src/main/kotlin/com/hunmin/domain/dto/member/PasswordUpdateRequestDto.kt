package com.hunmin.domain.dto.member

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class PasswordUpdateRequestDto (
    val email: @Email String,

    @field:NotBlank
    val nickname: String,

    @field:NotBlank
    val newPassword:String
)
