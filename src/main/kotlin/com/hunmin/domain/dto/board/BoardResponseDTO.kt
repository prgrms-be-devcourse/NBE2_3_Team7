package com.hunmin.domain.dto.board

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.hunmin.domain.dto.comment.CommentResponseDTO
import com.hunmin.domain.entity.Board
import java.time.LocalDateTime

data class BoardResponseDTO @JsonCreator constructor(
    val boardId: Long,
    val memberId: Long? = null,
    val title: String,
    val nickname: String,
    val profileImage: String? = null,
    val content: String,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrls: List<String> = emptyList(),

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val createdAt: LocalDateTime?,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val updatedAt: LocalDateTime?,

    val comments: List<CommentResponseDTO> = emptyList()
) {
    constructor(board: Board) : this(
        boardId = board.boardId,
        memberId = board.member?.memberId,
        title = board.title,
        nickname = board.member?.nickname ?: "Unknown",
        profileImage = board.member?.image,
        content = board.content,
        location = board.location,
        latitude = board.latitude,
        longitude = board.longitude,
        imageUrls = board.imageUrls ?: emptyList(),
        createdAt = board.createdAt,
        updatedAt = board.updatedAt,
        comments = board.comments.map { CommentResponseDTO(it) }
    )
}
