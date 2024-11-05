package com.hunmin.domain.controller

import com.hunmin.domain.dto.board.BoardRequestDTO
import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.exception.BoardException
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.BoardService
import com.hunmin.global.s3.S3FileManagement
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/board")
@Tag(name = "게시글", description = "게시글 CRUD")
class BoardController(
    private val boardService: BoardService,
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
    private val s3FileManagement: S3FileManagement // s3 주입
) {

    // 게시글 이미지 첨부
    @PostMapping("/uploadImage")
    @Operation(summary = "게시글 이미지 등록", description = "게시글에 여러 이미지를 등록할 때 사용하는 API")
    fun uploadImages(@RequestParam("files") files: Array<MultipartFile>): ResponseEntity<List<String>> {
        val imageUrls = mutableListOf<String>()

        return try {
            files.forEach { file ->
                // boardService.uploadImage() 대신 s3FileManagement 직접 사용
                val imageUrl = s3FileManagement.uploadImage(file)
                imageUrls.add(imageUrl)
            }
            ResponseEntity.ok(imageUrls)
        } catch (e: Exception) { // IOException 대신 일반 Exception으로 변경
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(listOf("Image upload failed"))
        }
    }

    // 게시글 등록
    @PostMapping
    @Operation(summary = "게시글 등록", description = "게시글을 등록할 때 사용하는 API")
    fun createBoard(@Valid @RequestBody boardRequestDTO: BoardRequestDTO): ResponseEntity<BoardResponseDTO> {
        return ResponseEntity.ok(boardService.createBoard(boardRequestDTO))
    }

    // 게시글 조회
    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 조회", description = "게시글을 조회할 때 사용하는 API")
    fun readBoard(@PathVariable boardId: Long): ResponseEntity<BoardResponseDTO> {
        return ResponseEntity.ok(boardService.readBoard(boardId))
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정할 때 사용하는 API")
    fun updateBoard(@PathVariable boardId: Long, @Valid @RequestBody boardRequestDTO: BoardRequestDTO, authentication: Authentication): ResponseEntity<BoardResponseDTO> {
        val id = memberRepository.findByEmail(authentication.name).memberId
        if (id != boardRequestDTO.memberId) {
            throw BoardException.NOT_UPDATED.toException()
        }
        return ResponseEntity.ok(boardService.updateBoard(boardId, boardRequestDTO))
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제할 때 사용하는 API")
    fun deleteBoard(@PathVariable boardId: Long, authentication: Authentication): ResponseEntity<Map<String, String>> {
        val id = memberRepository.findByEmail(authentication.name).memberId
        if (id != boardRepository.findById(boardId).get().member.memberId) {
            throw BoardException.NOT_DELETED.toException()
        }
        boardService.deleteBoard(boardId)
        return ResponseEntity.ok(mapOf("result" to "success"))
    }

    // 게시글 목록 조회
    @GetMapping
    @Operation(summary = "게시글 목록", description = "게시글 목록을 조회할 때 사용하는 API")
    fun readBoardList(
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "5") size: Int
    ): ResponseEntity<Page<BoardResponseDTO>> {
        val pageRequestDTO = PageRequestDTO(page = page, size = size)
        return ResponseEntity.ok(boardService.readBoardList(pageRequestDTO))
    }

    // 회원 별 작성글 목록 조회
    @GetMapping("/member/{memberId}")
    @Operation(summary = "회원 별 작성글 목록", description = "회원별 작성글 목록을 조회할 때 사용하는 API")
    fun readBoardListByMember(
        @PathVariable memberId: Long,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<Page<BoardResponseDTO>> {
        val pageRequestDTO = PageRequestDTO(page = page, size = size)
        return ResponseEntity.ok(boardService.readBoardListByMember(memberId, pageRequestDTO))
    }

    //검색별 게시글 조회
    @GetMapping("/search")
    @Operation(summary = "검색 별 작성글 목록", description = "검색별 작성글 목록을 조회할 때 사용하는 API")
    fun searchBoard(
        @Validated
        @RequestParam("title") title: String,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<Page<BoardResponseDTO>> {
        val pageRequestDTO = PageRequestDTO(page = page, size = size)
        val boardResponseDTOS: Page<BoardResponseDTO> = boardService.searchBoardByTitle(pageRequestDTO, title)
        return ResponseEntity.ok().body(boardResponseDTOS)
    }
}
