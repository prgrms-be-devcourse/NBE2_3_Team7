package com.hunmin.domain.repository

import com.hunmin.domain.entity.Bookmark
import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface BookmarkRepository : JpaRepository<Bookmark, Long> {
    //사용자와 게시글로 북마크 조회
    fun findByMemberAndBoard(member: Member, board: Board): Optional<Bookmark>

    //회원 별 북마크 게시글 목록 조회
    @Query("SELECT b.board FROM Bookmark b WHERE b.member.memberId = :memberId ORDER BY b.board.boardId DESC")
    fun findByMemberId(@Param("memberId") memberId: Long): List<Board>

    //북마크 여부 확인
    fun existsByMemberAndBoard(member: Member, board: Board): Boolean
}
