package com.hunmin.domain.controller

import com.hunmin.domain.dto.member.CustomUserDetails
import com.hunmin.domain.dto.word.WordPageRequestDTO
import com.hunmin.domain.dto.word.WordRequestDTO
import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.service.WordService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/words")
@Tag(name = "단어", description = "단어 CRUD")
class WordController (
    private val wordService: WordService
){
//    // 단어 등록
//    @PostMapping
//    @Operation(summary = "단어 등록", description = "단어를 등록할 때 사용하는 API")
//    fun createWord(@Validated @RequestBody wordRequestDTO: WordRequestDTO,
//                   @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<WordResponseDTO> {
//        val createdWord = wordService.createWord(wordRequestDTO, userDetails.getUsername())
//        return ResponseEntity.ok(createdWord)
//    }

//    // 단어 수정
//    @PutMapping("/update/{title}/{lang}")
//    @Operation(summary = "단어 수정", description = "단어를 수정할 때 사용하는 API")
//    fun updateWord(@Validated @RequestBody wordRequestDTO: WordRequestDTO,
//                   @RequestParam memberId: Long): ResponseEntity<WordResponseDTO> {
//        val updatedWord = wordService.updateWord(wordRequestDTO, memberId)
//        return ResponseEntity.ok(updatedWord)
//    }
//
//    // 단어 삭제
//    @DeleteMapping
//    @Operation(summary = "단어 삭제", description = "단어를 삭제할 때 사용하는 API")
//    fun deleteWord(@RequestParam title: String, @RequestParam lang: String,
//                   @RequestParam memberId: Long): ResponseEntity<Void> {
//        wordService.deleteWord(title, lang, memberId)
//        return ResponseEntity.noContent().build()
//    }
//
    // 단어 조회
    @GetMapping("/join/{title}/{lang}")
    @Operation(summary = "단어 조회", description = "단어를 조회할 때 사용하는 API")
    fun getWord(@PathVariable title: String,
                @PathVariable lang: String): ResponseEntity<WordResponseDTO> {
        val wordResponseDTO = wordService.getWord(title, lang)
        return ResponseEntity.ok(wordResponseDTO)
    }

    // 단어 전체 조회
    @GetMapping
    @Operation(summary = "단어 전체 조회", description = "모든 단어를 조회할 때 사용하는 API")
    fun getALLWords(
        @RequestParam lang: String,
        @Validated wordPageRequestDTO: WordPageRequestDTO): ResponseEntity<Page<WordResponseDTO>> {
        val pageable = wordPageRequestDTO.getPageable(Sort.by("title").ascending())
        val allWords = wordService.getAllWords(lang, pageable)
        return ResponseEntity.ok(allWords)
    }

    // 단어 등록 테스트
    @PostMapping
    fun testCreateWord(@Validated @RequestBody wordRequestDTO: WordRequestDTO): ResponseEntity<WordResponseDTO> {
        val createdWord = wordService.testCreate(wordRequestDTO)
        return ResponseEntity.ok(createdWord)
    }

    // 단어 수정 테스트
    @PutMapping("/update/{title}/{lang}")
    fun testUpdateWord(@Validated @RequestBody wordRequestDTO: WordRequestDTO): ResponseEntity<WordResponseDTO> {
        val updatedWord = wordService.testUpdate(wordRequestDTO)
        return ResponseEntity.ok(updatedWord)
    }

    // 단어 삭제 테스트
    @DeleteMapping("/delete/{title}/{lang}")
    fun testDelete(@PathVariable title: String, @PathVariable lang: String): ResponseEntity<Map<String, String>> {
        wordService.testDelete(title, lang)
        return ResponseEntity.ok(mapOf("result" to "success"))
    }
}