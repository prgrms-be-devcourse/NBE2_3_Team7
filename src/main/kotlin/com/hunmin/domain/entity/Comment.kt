package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board: Board? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null,

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    var children: MutableList<Comment> = mutableListOf()

) : BaseTimeEntity() {
    fun changeContent(content: String) {
        this.content = content
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
        return "Comment(commentId=$commentId, content=$content)"
    }
}
