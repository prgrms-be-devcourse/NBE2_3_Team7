package com.hunmin.domain.controller

import com.hunmin.domain.dto.notice.NoticePageRequestDTO
import com.hunmin.domain.dto.notice.NoticeRequestDTO
import com.hunmin.domain.dto.notice.NoticeResponseDTO
import com.hunmin.domain.dto.notice.NoticeUpdateDTO
import com.hunmin.domain.service.NoticeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notices")
@Tag(name = "공지사항", description = "공지사항 CRUD")
class NoticeController(
    private val noticeService: NoticeService
) {

    @GetMapping("/list/{page}")
    @Operation(summary = "페이지 조회", description = "공지사항을 페이지로 조회할때 사용하는 API")
    fun getNoticeList(@Validated noticePageRequestDTO: NoticePageRequestDTO, @PathVariable page: Int): ResponseEntity<Page<NoticeResponseDTO>> {
        noticePageRequestDTO.page = page
        val noticeList = noticeService.getAllNotices(noticePageRequestDTO)
        return ResponseEntity.ok(noticeList)
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지 조회", description = "공지사항을 조회할때 사용하는 API")
    fun getNotice(@PathVariable noticeId: Long): ResponseEntity<NoticeResponseDTO> {
        val notice = noticeService.getNoticeById(noticeId)
        return ResponseEntity.ok(notice)
    }

    @PostMapping
    @Operation(summary = "공지 등록", description = "공지사항을 등록할때 사용하는 API")
    fun createNotice(@Validated @RequestBody noticeRequestDTO: NoticeRequestDTO,
        @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<NoticeResponseDTO> {
        val notice = noticeService.createNotice(noticeRequestDTO, userDetails.username)
        return ResponseEntity.ok(notice)
    }

    @PutMapping("/{noticeId}")
    @Operation(summary = "공지 수정", description = "공지사항을 수정할때 사용하는 API")
    fun updateNotice(@PathVariable noticeId: Long, @Validated @RequestBody noticeUpdateDTO: NoticeUpdateDTO,
        @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<NoticeResponseDTO> {
        val notice = noticeService.updateNotice(noticeUpdateDTO, userDetails.username, noticeId)
        return ResponseEntity.ok(notice)
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "공지 삭제", description = "공지사항을 삭제할때 사용하는 API")
    fun deleteNotice(@PathVariable noticeId: Long, @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Map<String, String>> {
        val isDeleted = noticeService.deleteNotice(noticeId, userDetails.username)
        val result = if (isDeleted) {
            mapOf("result" to "success")
        } else {
            mapOf("result" to "fail")
        }
        return ResponseEntity.ok(result)
    }
}