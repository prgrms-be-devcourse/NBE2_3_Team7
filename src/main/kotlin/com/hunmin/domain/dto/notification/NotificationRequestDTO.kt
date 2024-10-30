package com.hunmin.domain.dto.notification

import jakarta.validation.constraints.NotNull

data class BoardRequestDTO(
    @field:NotNull(message = "게시글 ID는 필수항목입니다.")
    val boardId: Long,

    @field:NotNull(message = "회원 ID는 필수항목입니다.")
    val memberId: Long
)