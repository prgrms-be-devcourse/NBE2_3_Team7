package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class Bookmark(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val bookmarkId: Long = 0,

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member,

    @JoinColumn(name = "board_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var board: Board,
) {
    class Builder {
        private var bookmarkId: Long = 0
        private var member: Member? = null
        private var board: Board? = null

        fun bookmarkId(bookmarkId: Long) = apply { this.bookmarkId = bookmarkId }
        fun member(member: Member) = apply { this.member = member }
        fun board(board: Board) = apply { this.board = board }

        fun build(): Bookmark {
            val member = member ?: throw IllegalArgumentException("회원은 필수")
            val board = board ?: throw IllegalArgumentException("게시글은 필수")
            return Bookmark(
                bookmarkId = bookmarkId,
                member = member,
                board = board
            )
        }
    }

    companion object {
        fun builder() = Builder()
    }

    override fun hashCode(): Int {
        return bookmarkId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bookmark) return false
        return bookmarkId == other.bookmarkId
    }

    override fun toString(): String {
        return "Bookmark(bookmarkId=$bookmarkId, board=$board)"
    }
}
