package com.hunmin.domain.controller

import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.LikeCommentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/likeComment")
@Tag(name = "댓글 좋아요", description = "좋아요 CRUD")
class LikeCommentController(
    private val likeCommentService: LikeCommentService,
    private val memberRepository: MemberRepository
) {

    //좋아요 등록
    @PostMapping("/{commentId}")
    @Operation(summary = "좋아요 등록", description = "댓글 좋아요를 등록할 때 사용하는 API")
    fun createLikeComment(@PathVariable commentId: Long,
//                          authentication: Authentication
    ): ResponseEntity<String> {
//        val memberId = memberRepository.findByEmail(authentication.name).memberId
        val memberId = 1L
        likeCommentService.createLikeComment(memberId, commentId)
        return ResponseEntity.ok().build()
    }

    //좋아요 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "좋아요 삭제", description = "댓글 좋아요를 삭제할 때 사용하는 API")
    fun deleteLikeComment(@PathVariable commentId: Long,
//                          authentication: Authentication
    ): ResponseEntity<String> {
//        val memberId = memberRepository.findByEmail(authentication.name).memberId
        val memberId = 1L
        likeCommentService.deleteLikeComment(memberId, commentId)
        return ResponseEntity.ok().build()
    }

    //좋아요 여부 확인
    @GetMapping("/{commentId}/member/{memberId}")
    @Operation(summary = "좋아요 여부 확인", description = "댓글 좋아요 등록 여부를 확인할 때 사용하는 API")
    fun isLikedComment(@PathVariable commentId: Long, @PathVariable memberId: Long): ResponseEntity<Boolean> {
        val isLikedComment = likeCommentService.isLikeComment(memberId, commentId)
        return ResponseEntity.ok(isLikedComment)
    }

    //좋아요 누른 사용자 목록 조회
    @GetMapping("/{commentId}/members")
    @Operation(summary = "좋아요 누른 사용자 목록", description = "좋아요 누른 사용자 목록을 조회할 때 사용하는 API")
    fun getLikeCommentMembers(@PathVariable commentId: Long): ResponseEntity<List<Map<String, String>>> {
        val members = likeCommentService.getLikeCommentMembers(commentId)
        return ResponseEntity.ok(members)
    }
}
