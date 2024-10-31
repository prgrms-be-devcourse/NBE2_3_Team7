package com.hunmin.domain.service

import com.hunmin.domain.dto.comment.CommentRequestDTO
import com.hunmin.domain.dto.comment.CommentResponseDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Comment
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.BoardException
import com.hunmin.domain.exception.CommentException
import com.hunmin.domain.exception.MemberException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Service
@Transactional
class CommentService(
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters
) {
    //댓글 등록
    fun createComment(commentRequestDTO: CommentRequestDTO): CommentResponseDTO {
        return try{
            val member = memberRepository.findById(commentRequestDTO.memberId).orElseThrow { MemberException.NOT_FOUND.get() }
            val board = boardRepository.findById(commentRequestDTO.boardId).orElseThrow{ BoardException.NOT_FOUND.toException() }

            val comment = Comment.builder()
                .member(member)
                .board(board)
                .content(commentRequestDTO.content)
                .build()

            commentRepository.save(comment)

            val boardMemberId = comment.board.member.memberId
            val message = "[${board.title}] 새로운 댓글 : ${comment.content}"

            if (boardMemberId != member.memberId) {
                val notificationSendDTO = NotificationSendDTO(
                    memberId = boardMemberId,
                    message = message,
                    notificationType = NotificationType.COMMENT,
                    url = "/board/${board.boardId}"
                )

                notificationService.send(notificationSendDTO)
            }

            val emitterId = "${boardMemberId}_"
            val emitter = sseEmitters.findSingleEmitter(emitterId)

            if (emitter != null) {
                try {
                    emitter.send(CommentResponseDTO(comment))
                } catch (e: IOException) {
                    sseEmitters.delete(emitterId)
                }
            }

            CommentResponseDTO(comment)
        } catch (e: Exception) {
            throw CommentException.NOT_CREATED.toException()
        }
    }

    //대댓글 등록
    fun createCommentChild(boardId: Long, commentId: Long, commentRequestDTO: CommentRequestDTO): CommentResponseDTO {
        return try{
            val member = memberRepository.findById(commentRequestDTO.memberId).orElseThrow { MemberException.NOT_FOUND.get() }
            val board = boardRepository.findById(boardId).orElseThrow{ BoardException.NOT_FOUND.toException() }
            val parent = commentRepository.findById(commentId).orElseThrow{ CommentException.NOT_FOUND.toException() }

            val child = Comment.builder()
                .member(member)
                .board(board)
                .parent(parent)
                .content(commentRequestDTO.content)
                .build()

            commentRepository.save(child)

            val parentMemberId = parent.member.memberId
            val message = "[${board.title}] 에 작성한 댓글 '${parent.content}'에 새로운 대댓글 : ${child.content}"

            if (parentMemberId != child.member.memberId) {
                val notificationSendDTO = NotificationSendDTO(
                    memberId = parentMemberId,
                    message = message,
                    notificationType = NotificationType.COMMENT,
                    url = "/board/${board.boardId}"
                )

                notificationService.send(notificationSendDTO)
            }

            val emitterId = "${parentMemberId}_"
            val emitter = sseEmitters.findSingleEmitter(emitterId)

            if (emitter != null) {
                try {
                    emitter.send(CommentResponseDTO(child))
                } catch (e: IOException) {
                    sseEmitters.delete(emitterId)
                }
            }

            CommentResponseDTO(child)
        } catch (e: Exception) {
            throw CommentException.NOT_CREATED.toException()
        }
    }

    //댓글 수정
    fun updateComment(commentId: Long, commentRequestDTO: CommentRequestDTO): CommentResponseDTO {
        val comment = commentRepository.findById(commentId).orElseThrow{ CommentException.NOT_FOUND.toException() }

        return try {
            comment.changeContent(commentRequestDTO.content)

            commentRepository.save(comment)

            CommentResponseDTO(comment)
        } catch (e: Exception) {
            throw CommentException.NOT_UPDATED.toException()
        }
    }

    //댓글 삭제
    fun deleteComment(commentId: Long): CommentResponseDTO {
        val comment = commentRepository.findById(commentId).orElseThrow{ CommentException.NOT_FOUND.toException() }

        return try {
            commentRepository.delete(comment)

            CommentResponseDTO(comment)
        } catch(e: Exception) {
            throw CommentException.NOT_DELETED.toException()
        }
    }

    //게시글 별 댓글 목록 조회
    fun readCommentList(boardId: Long, pageRequestDTO: PageRequestDTO): Page<CommentResponseDTO> {
        val sort = Sort.by(Sort.Direction.ASC, "commentId")
        val pageable: Pageable = pageRequestDTO.getPageable(sort)

        return commentRepository.findByBoardId(boardId, pageable).map { CommentResponseDTO(it) }
    }
}
