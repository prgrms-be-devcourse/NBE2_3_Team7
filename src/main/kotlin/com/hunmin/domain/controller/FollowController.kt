package com.hunmin.domain.controller

import com.hunmin.domain.dto.follow.FollowRequestDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.service.FollowService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/follow")
@RestController
@Tag(name = "팔로우", description = "팔로우 CRUD")
class FollowController (
    private val followService: FollowService
){

    // 팔로이 등록 요청
    @PostMapping("/{memberId}")
    @Operation(summary = "팔로이 등록 요청", description = "팔로이 등록 요청 호출 API")
    fun registerFollower(
        @Validated
        @PathVariable memberId: Long,
        authentication: Authentication
    ): ResponseEntity<FollowRequestDTO> {
        val myEmail = authentication.name
        return ResponseEntity.ok(followService.register(myEmail, memberId))

    }

    // 팔로이 수락 요청
    @GetMapping("/{memberId}")
    @Operation(summary = "팔로이 수락 요청", description = "팔로이 수락 요청 호출 API")
    fun acceptFollower(
        @Validated
        @PathVariable memberId: Long,
        authentication: Authentication
    ): ResponseEntity<FollowRequestDTO> {
        val myEmail = authentication.name
        return ResponseEntity.ok(followService.registerAccept(myEmail, memberId))
    }

    // 팔로이 삭제
    @DeleteMapping("/{memberId}")
    @Operation(summary = "팔로이 삭제", description = "팔로이 삭제 호출 API")
    fun deleteFollower(
        @Validated
        @PathVariable memberId: Long,
        authentication: Authentication
    ): ResponseEntity<Boolean> {
        val myEmail = authentication.name
        return ResponseEntity.ok(followService.remove(myEmail, memberId))
    }

    // 팔로우 리스트 조회
    @GetMapping("/list")
    @Operation(summary = "팔로이 리스트 조회", description = "팔로이 리스트 호출 API")
    fun loadMessageList(
        @Validated
        authentication: Authentication,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<Page<FollowRequestDTO>> {
        val email = authentication.name
        val pageRequestDTO = PageRequestDTO(page=page, size=size)
        return ResponseEntity.ok(followService.readPage(pageRequestDTO, email))

    }

    // 알림 변경 요청
    @PostMapping("/notification/{memberId}")
    @Operation(summary = "알림 변경 요청", description = "알림 변경 요청 호출 API")
    fun changeNotification(
        @Validated
        @PathVariable memberId: Long,
        authentication: Authentication
    ): ResponseEntity<Boolean> {
        val myEmail = authentication.name
        return ResponseEntity.ok(followService.turnNotification(myEmail, memberId))
    }

    // 차단 상태 변경 요청
    @PostMapping("/block/{memberId}")
    @Operation(summary = "차단 상태 변경 요청", description = "차단 상태 변경 요청 호출 API")
    fun blockFollower(
        @PathVariable memberId: Long,
        authentication: Authentication
    ): ResponseEntity<Boolean> {
        val myEmail = authentication.name
        return ResponseEntity.ok(followService.blockFollower(myEmail, memberId))
    }

    // follow 상태 확인 요청
    @GetMapping("/check/{targetMemberId}")
    @Operation(summary = "follow 상태 확인 요청", description = "follow 상태 확인 요청 호출 API")
    fun checkFollowStatus(
        @RequestParam("memberId") memberId: Long,
        @PathVariable("targetMemberId") targetMemberId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val isFollowing: Boolean = followService.isFollowing(memberId, targetMemberId)
        val response = mapOf("isFollowing" to isFollowing)
        return ResponseEntity.ok(response)
    }
}
