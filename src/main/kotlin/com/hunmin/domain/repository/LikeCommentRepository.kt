package com.hunmin.domain.repository

import com.hunmin.domain.entity.Comment
import com.hunmin.domain.entity.LikeComment
import com.hunmin.domain.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface LikeCommentRepository : JpaRepository<LikeComment, Long> {
    //사용자와 댓글로 좋아요 조회
    fun findByMemberAndComment(member: Member, comment: Comment): Optional<LikeComment>

    //좋아요 여부 확인
    fun existsByMemberAndComment(member: Member, comment: Comment): Boolean

    //좋아요 누른 사용자 목록 조회
    @Query("SELECT l.member FROM LikeComment l WHERE l.comment.commentId = :commentId")
    fun findMembersByLikedCommentId(@Param("commentId") commentId: Long): List<Member>
}
