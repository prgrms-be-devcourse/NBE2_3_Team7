package com.hunmin.domain.entity

import com.hunmin.domain.entity.Board.Builder
import jakarta.persistence.*

@Entity
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentId: Long = 0,

    @JoinColumn(name = "board_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var board: Board,

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member,

    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null,

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    var children: MutableList<Comment> = mutableListOf(),

    var likeCount: Int = 0

) : BaseTimeEntity() {
    class Builder {
        private var commentId: Long = 0
        private var board: Board? = null
        private var member: Member? = null
        private var content: String = ""
        private var parent: Comment? = null
        private var children: MutableList<Comment> = mutableListOf()

        fun commentId(commentId: Long) = apply { this.commentId = commentId }
        fun board(board: Board?) = apply { this.board = board }
        fun member(member: Member?) = apply { this.member = member }
        fun content(content: String) = apply { this.content = content }
        fun parent(parent: Comment?) = apply { this.parent = parent }
        fun children(children: MutableList<Comment>) = apply { this.children = children }
        private var likeCount: Int = 0

        fun build(): Comment {
            val member = member ?: throw IllegalArgumentException("회원은 필수")
            val board = board ?: throw IllegalArgumentException("게시글은 필수")
            return Comment(
                commentId = commentId,
                board = board,
                member = member,
                content = content,
                parent = parent,
                children = children,
                likeCount = likeCount
            )
        }
    }

    companion object {
        fun builder() = Builder()
    }

    fun changeContent(content: String) {
        this.content = content
    }

    fun incrementLikeCount() {
        this.likeCount++
    }

    fun decrementLikeCount() {
        if (likeCount > 0) this.likeCount--
    }

    override fun hashCode(): Int {
        return commentId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Comment) return false
        return commentId == other.commentId
    }

    override fun toString(): String {
        return "Comment(commentId=$commentId, content=$content, likeCount=$likeCount)"
    }
}
