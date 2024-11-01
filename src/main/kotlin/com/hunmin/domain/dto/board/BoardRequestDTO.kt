package com.hunmin.domain.dto.board

import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Member
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class BoardRequestDTO(
    @field:NotNull(message = "게시글 ID는 필수항목입니다.")
    val boardId: Long,

    @field:NotNull(message = "회원 ID는 필수항목입니다.")
    val memberId: Long,

    @field:NotBlank(message = "제목은 필수항목입니다.")
    val title: String,

    @field:NotBlank(message = "내용은 필수항목입니다.")
    val content: String,

    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrls: MutableList<String>? = null
) {
    fun toEntity(member: Member): Board = Board(
        boardId = boardId,
        member = member,
        title = title,
        nickname = member.nickname,
        content = content,
        location = location,
        latitude = latitude,
        longitude = longitude,
        imageUrls = imageUrls ?: mutableListOf()
    )
}
