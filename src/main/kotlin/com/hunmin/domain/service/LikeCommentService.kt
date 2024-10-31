package com.hunmin.domain.service

import com.hunmin.domain.dto.comment.CommentResponseDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.entity.*
import com.hunmin.domain.exception.CommentException
import com.hunmin.domain.exception.LikeCommentException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.LikeCommentRepository
import com.hunmin.domain.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Service
@Transactional
class LikeCommentService(
    private val likeCommentRepository: LikeCommentRepository,
    private val memberRepository: MemberRepository,
    private val commentRepository: CommentRepository,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters
) {

    //좋아요 등록
    fun createLikeComment(memberId: Long, commentId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow()
        val comment = commentRepository.findById(commentId).orElseThrow { CommentException.NOT_FOUND.toException() }

        likeCommentRepository.findByMemberAndComment(member, comment).ifPresentOrElse(
            {
                throw LikeCommentException.NOT_CREATED.toException()
            },
            {
                likeCommentRepository.save(LikeComment.builder().member(member).comment(comment).build())
                comment.incrementLikeCount()

                val commentMemberId = comment.member.memberId
                val message = "[${comment.board.title}] 에 작성한 댓글 '${comment.content}'에 ${member.nickname} 님의 좋아요"

                if (commentMemberId != member.memberId) {
                    val notificationSendDTO = NotificationSendDTO(
                        memberId = commentMemberId,
                        message = message,
                        notificationType = NotificationType.COMMENT,
                        url = "/board/${comment.board.boardId}"
                    )

                    notificationService.send(notificationSendDTO)
                }

                val emitterId = "${commentMemberId}_"
                val emitter = sseEmitters.findSingleEmitter(emitterId)

                if (emitter != null) {
                    try {
                        emitter.send(CommentResponseDTO(comment))
                    } catch (e: IOException) {
                        sseEmitters.delete(emitterId)
                    }
                }
            }
        )
    }

    //좋아요 삭제
    fun deleteLikeComment(memberId: Long, commentId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow()
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
        val member = memberRepository.findById(memberId).orElseThrow()
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
