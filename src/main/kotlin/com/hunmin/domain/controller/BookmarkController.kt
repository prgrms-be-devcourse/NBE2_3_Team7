package com.hunmin.domain.controller

import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.BookmarkService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookmark")
@Tag(name = "게시글 북마크", description = "북마크 CRUD")
class BookmarkController(
    private val bookmarkService: BookmarkService,
    private val memberRepository: MemberRepository
) {

    //북마크 등록
    @PostMapping("/{boardId}")
    @Operation(summary = "북마크 등록", description = "게시글 북마크를 등록할 때 사용하는 API")
    fun createBookmark(@PathVariable boardId: Long,
//                       authentication: Authentication
    ): ResponseEntity<String> {
//        val memberId = memberRepository.findByEmail(authentication.name).memberId
        val memberId = 1L
        bookmarkService.createBookmark(boardId, memberId)
        return ResponseEntity.ok("북마크 등록")
    }

    //북마크 삭제
    @DeleteMapping("/{boardId}")
    @Operation(summary = "북마크 삭제", description = "게시글 북마크를 삭제할 때 사용하는 API")
    fun deleteBookmark(@PathVariable boardId: Long,
//                       authentication: Authentication
    ): ResponseEntity<String> {
//        val memberId = memberRepository.findByEmail(authentication.name).memberId
        val memberId = 1L
        bookmarkService.deleteBookmark(boardId, memberId)
        return ResponseEntity.ok("북마크 삭제")
    }

    //회원 별 북마크 게시글 목록 조회
    @GetMapping("/member/{memberId}")
    @Operation(summary = "회원 별 북마크 게시글 목록", description = "회원별 북마크 게시글 목록을 조회할 때 사용하는 API")
    fun getBookmarkedBoards(@PathVariable memberId: Long): ResponseEntity<List<BoardResponseDTO>> {
        val bookmarkedBoards = bookmarkService.readBookmarkByMember(memberId)
        return ResponseEntity.ok(bookmarkedBoards)
    }

    //북마크 여부 확인
    @GetMapping("/{boardId}/member/{memberId}")
    @Operation(summary = "북마크 여부 확인", description = "게시글 북마크 등록 여부를 확인할 때 사용하는 API")
    fun isBookmarked(@PathVariable boardId: Long, @PathVariable memberId: Long): ResponseEntity<Boolean> {
        val isBookmarked = bookmarkService.isBookmarked(boardId, memberId)
        return ResponseEntity.ok(isBookmarked)
    }
}
