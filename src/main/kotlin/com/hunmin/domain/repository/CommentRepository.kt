package com.hunmin.domain.repository

import com.hunmin.domain.entity.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository : JpaRepository<Comment, Long> {

    // 게시글 별 댓글 목록 조회
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.children WHERE c.board.boardId = :boardId AND c.parent IS NULL")
    fun findByBoardId(@Param("boardId") boardId: Long, pageable: Pageable): Page<Comment>

    // 회원 별 댓글 수 조회
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.member.memberId = :memberId")
    fun countByMemberId(@Param("memberId") memberId: Long): Int

    // 회원 별 댓글 목록 조회
    @Query("SELECT c FROM Comment c WHERE c.member.memberId = :memberId")
    fun findByMemberId(@Param("memberId") memberId: Long, pageable: Pageable): Page<Comment>
}