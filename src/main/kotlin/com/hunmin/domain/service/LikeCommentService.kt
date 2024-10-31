package com.hunmin.domain.service

import com.hunmin.domain.entity.*
import com.hunmin.domain.exception.CommentException
import com.hunmin.domain.exception.LikeCommentException
import com.hunmin.domain.exception.MemberException
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.LikeCommentRepository
import com.hunmin.domain.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeCommentService(
    private val likeCommentRepository: LikeCommentRepository,
    private val memberRepository: MemberRepository,
    private val commentRepository: CommentRepository
) {

    //좋아요 등록
    @Transactional
    fun createLikeComment(memberId: Long, commentId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
        val comment = commentRepository.findById(commentId).orElseThrow { CommentException.NOT_FOUND.toException() }

        likeCommentRepository.findByMemberAndComment(member, comment).ifPresentOrElse(
            {
                throw LikeCommentException.NOT_CREATED.toException()
            },
            {
                likeCommentRepository.save(LikeComment.builder().member(member).comment(comment).build())
                comment.incrementLikeCount()
            }
        )
    }

    //좋아요 삭제
    @Transactional
    fun deleteLikeComment(memberId: Long, commentId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
        val comment = commentRepository.findById(commentId).orElseThrow { CommentException.NOT_FOUND.toException() }
        val likeComment = likeCommentRepository.findByMemberAndComment(member, comment).orElseThrow { LikeCommentException.NOT_FOUND.toException() }

        try {
            likeCommentRepository.delete(likeComment)
            comment.decrementLikeCount()
        } catch (e: Exception) {
            throw LikeCommentException.NOT_DELETED.toException()
        }
    }

    //좋아요 여부 확인
    fun isLikeComment(memberId: Long, commentId: Long): Boolean {
        val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
        val comment = commentRepository.findById(commentId).orElseThrow { CommentException.NOT_FOUND.toException() }

        return likeCommentRepository.existsByMemberAndComment(member, comment)
    }

    //좋아요 누른 사용자 목록 조회
    fun getLikeCommentMembers(commentId: Long): List<Map<String, String>> {
        val members = likeCommentRepository.findMembersByLikedCommentId(commentId)
        return members.map { member ->
            mapOf(
                "nickname" to member.nickname,
                "image" to (member.image ?: "")
            )
        }
    }
}
