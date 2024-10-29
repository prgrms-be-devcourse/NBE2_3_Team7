package com.hunmin.domain.repository

import com.hunmin.domain.entity.Board
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface BoardRepository : JpaRepository<Board, Long> {

    //회원 별 작성글 목록 조회
    @Query("SELECT b FROM Board b WHERE b.member.memberId = :memberId")
    fun findByMemberId(@Param("memberId") memberId: Long, pageable: Pageable): Page<Board>

    //게시글과 댓글을 같이 조회
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.comments WHERE b.boardId = :boardId")
    fun findByIdWithComments(@Param("boardId") boardId: Long): Optional<Board>

    //회원 별 게시글 수 조회
    @Query("SELECT COUNT(*) FROM Board b WHERE b.member.memberId = :memberId")
    fun countByMemberId(@Param("memberId") memberId: Long): Int
}
