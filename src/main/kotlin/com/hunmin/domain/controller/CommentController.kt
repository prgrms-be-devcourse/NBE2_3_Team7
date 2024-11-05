package com.hunmin.domain.controller

import CommentResponseDTO
import com.hunmin.domain.dto.comment.CommentRequestDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.exception.CommentException
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.CommentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/board/{boardId}/comment")
@Tag(name = "댓글", description = "댓글 CRUD")
class CommentController(
    private val commentService: CommentService,
    private val memberRepository: MemberRepository,
    private val commentRepository: CommentRepository
) {

    // 댓글 등록
    @PostMapping
    @Operation(summary = "댓글 등록", description = "댓글을 등록할 때 사용하는 API")
    fun createComment(@PathVariable boardId: Long, @Valid @RequestBody commentRequestDTO: CommentRequestDTO): ResponseEntity<CommentResponseDTO> {
        return ResponseEntity.ok(commentService.createComment(commentRequestDTO))
    }

    // 대댓글 등록
    @PostMapping("/{commentId}")
    @Operation(summary = "대댓글 등록", description = "대댓글을 등록할 때 사용하는 API")
    fun createCommentChild(@PathVariable boardId: Long, @PathVariable commentId: Long,
                           @Valid @RequestBody commentRequestDTO: CommentRequestDTO): ResponseEntity<CommentResponseDTO> {
        return ResponseEntity.ok(commentService.createCommentChild(boardId, commentId, commentRequestDTO))
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정할 때 사용하는 API")
    fun updateComment(@PathVariable boardId: Long, @PathVariable commentId: Long,
                      @Valid @RequestBody commentRequestDTO: CommentRequestDTO, authentication: Authentication): ResponseEntity<CommentResponseDTO> {
        val id = memberRepository.findByEmail(authentication.name).memberId

        if (id != commentRequestDTO.memberId) {
            throw CommentException.NOT_UPDATED.toException()
        }
        return ResponseEntity.ok(commentService.updateComment(commentId, commentRequestDTO))
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제할 때 사용하는 API")
    fun deleteComment(@PathVariable boardId: Long, @PathVariable commentId: Long, authentication: Authentication): ResponseEntity<Map<String, String>> {
        val id = memberRepository.findByEmail(authentication.name).memberId

        if (id != commentRepository.findById(commentId).get().member.memberId) {
            throw CommentException.NOT_DELETED.toException()
        }
        commentService.deleteComment(commentId)
        return ResponseEntity.ok(mapOf("result" to "success"))
    }

    // 게시글 별 댓글 목록 조회
    @GetMapping
    @Operation(summary = "댓글 목록", description = "댓글 목록을 조회할 때 사용하는 API")
    fun readCommentList(
        @PathVariable boardId: Long,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int): ResponseEntity<Page<CommentResponseDTO>> {
        val pageRequestDTO = PageRequestDTO(page = page, size = size)
        return ResponseEntity.ok(commentService.readCommentList(boardId, pageRequestDTO))
    }
}
