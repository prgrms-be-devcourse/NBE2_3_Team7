package com.hunmin.domain.dto.comment

import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Comment
import com.hunmin.domain.entity.Member
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CommentRequestDTO(
    @field:NotNull(message = "댓글 ID는 필수항목입니다.")
    val commentId: Long,

    @field:NotNull(message = "게시글 ID는 필수항목입니다.")
    val boardId: Long,

    @field:NotNull(message = "회원 ID는 필수항목입니다.")
    val memberId: Long,

    @field:NotBlank(message = "댓글 내용은 필수항목입니다.")
    val content: String
) {
    fun toEntity(board: Board, member: Member): Comment = Comment(
        commentId = commentId,
        board = board,
        member = member,
        content = content
    )
}
