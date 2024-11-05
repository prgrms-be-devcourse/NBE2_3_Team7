package com.hunmin.domain.dto.board

import CommentResponseDTO
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.hunmin.domain.entity.Board
import java.time.LocalDateTime

data class BoardResponseDTO @JsonCreator constructor(
    @JsonProperty("boardId") val boardId: Long,
    @JsonProperty("memberId") val memberId: Long? = null,
    @JsonProperty("title") val title: String,
    @JsonProperty("nickname") val nickname: String,
    @JsonProperty("profileImage") val profileImage: String? = null,
    @JsonProperty("content") val content: String,
    @JsonProperty("location") val location: String?,
    @JsonProperty("latitude") val latitude: Double?,
    @JsonProperty("longitude") val longitude: Double?,
    @JsonProperty("imageUrls") val imageUrls: List<String> = emptyList(),

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonProperty("createdAt") val createdAt: LocalDateTime?,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonProperty("updatedAt") val updatedAt: LocalDateTime?,

    @JsonProperty("comments") val comments: List<CommentResponseDTO> = emptyList()
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
