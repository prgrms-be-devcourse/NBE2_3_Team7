import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.hunmin.domain.entity.Comment
import java.time.LocalDateTime

data class CommentResponseDTO @JsonCreator constructor(
    @JsonProperty("commentId") val commentId: Long,
    @JsonProperty("boardId") val boardId: Long? = null,
    @JsonProperty("memberId") val memberId: Long? = null,
    @JsonProperty("content") val content: String,
    @JsonProperty("createdAt")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("updatedAt")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val updatedAt: LocalDateTime? = null,
    @JsonProperty("nickname") val nickname: String = "Unknown",
    @JsonProperty("profileImage") val profileImage: String? = null,
    @JsonProperty("children") val children: List<CommentResponseDTO> = emptyList(),
    @JsonProperty("likeCount") val likeCount: Int = 0
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
