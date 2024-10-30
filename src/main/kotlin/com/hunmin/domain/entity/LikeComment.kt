package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class LikeComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val likeCommentId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    var comment: Comment
) {
    class Builder {
        private var likeCommentId: Long = 0
        private var member: Member? = null
        private var comment: Comment? = null

        fun likeCommentId(likeCommentId: Long) = apply { this.likeCommentId = likeCommentId }
        fun member(member: Member) = apply { this.member = member }
        fun comment(comment: Comment) = apply { this.comment = comment }

        fun build(): LikeComment {
            val member = member ?: throw IllegalArgumentException("회원은 필수")
            val comment = comment ?: throw IllegalArgumentException("댓글은 필수")
            return LikeComment(
                likeCommentId = likeCommentId,
                member = member,
                comment = comment
            )
        }
    }

    companion object {
        fun builder() = Builder()
    }

    override fun hashCode(): Int {
        return likeCommentId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LikeComment) return false
        return likeCommentId == other.likeCommentId
    }

    override fun toString(): String {
        return "LikeComment(likeCommentId=$likeCommentId, member=$member, comment=$comment)"
    }
}
