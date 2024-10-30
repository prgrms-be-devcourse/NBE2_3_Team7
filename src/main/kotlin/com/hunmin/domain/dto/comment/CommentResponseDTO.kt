package com.hunmin.domain.dto.comment

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.hunmin.domain.entity.Comment
import java.time.LocalDateTime

data class CommentResponseDTO(
    val commentId: Long,
    val boardId: Long? = null,
    val memberId: Long? = null,
    val content: String,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val createdAt: LocalDateTime? = null,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val updatedAt: LocalDateTime? = null,

    val nickname: String = "Unknown",
    val profileImage: String? = null,
    val children: List<CommentResponseDTO> = emptyList(),
    val likeCount: Int = 0
) {
    constructor(comment: Comment) : this(
        commentId = comment.commentId,
        boardId = comment.board?.boardId,
        memberId = comment.member?.memberId,
        content = comment.content,
        createdAt = comment.createdAt,
        updatedAt = comment.updatedAt,
        nickname = comment.member?.nickname ?: "Unknown",
        profileImage = comment.member?.image,
        children = comment.children?.sortedBy { it.commentId }?.map { CommentResponseDTO(it) } ?: emptyList(),
        likeCount = comment.likeCount
    )
}
