package com.hunmin.domain.controller

import CommentResponseDTO
import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.dto.member.MemberStatusDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.service.AdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/admin")
@Tag(name = "관리자", description = "관리자 기능")
@RestController
class AdminController(
    private val adminService: AdminService
) {
//    @GetMapping("")
//    fun adminP() = "ADMIN USER CONTROLLER"

    @Operation(summary = "회원 검색", description = "회원을 ID로 검색할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}")
    fun getMemberById(@PathVariable memberId: Long): ResponseEntity<MemberStatusDTO> {
        val memberStatus = adminService.getMemberByMemberId(memberId)
        return ResponseEntity.ok(memberStatus)
    }
    @Operation(summary = "회원 검색", description = "회원을 닉네임으로 검색할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/nickname/{username}")
    fun getMemberByNickname(@PathVariable username: String): ResponseEntity<MemberStatusDTO> {
        val memberStatus = adminService.getMemberByNickname(username)
        return ResponseEntity.ok(memberStatus)
    }

    @Operation(summary = "회원 조회", description = "회원을 목록을 조회할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members")
    fun getAllMembers(pageRequestDTO: PageRequestDTO): ResponseEntity<Page<MemberStatusDTO>> {
        val members = adminService.getAllMembers(pageRequestDTO)
        return ResponseEntity.ok(members)
    }

    @Operation(summary = "회원 작성글 조회", description = "특정 회원의 작성글 목록을 조회할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}/boards")
    fun getBoardsByMemberId(
        @PathVariable memberId: Long,
        pageRequestDTO: PageRequestDTO
    ): ResponseEntity<Page<BoardResponseDTO>> {
        val boards = adminService.getBoardsByMemberId(memberId, pageRequestDTO)
        return ResponseEntity.ok(boards)
    }

    @Operation(summary = "회원 작성댓글 조회", description = "특정 회원의 작성글 목록을 조회할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}/comments")
    fun getCommentsByMemberId(
        @PathVariable memberId: Long,
        pageRequestDTO: PageRequestDTO
    ): ResponseEntity<Page<CommentResponseDTO>> {
        val comments = adminService.getCommentsByMemberId(memberId, pageRequestDTO)
        return ResponseEntity.ok(comments)
    }
}

