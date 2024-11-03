package com.hunmin.domain.controller

import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.dto.word.WordScoreRequestDTO
import com.hunmin.domain.dto.word.WordScoreResponseDTO
import com.hunmin.domain.service.WordService
import com.hunmin.domain.service.WordTestService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/words/test")
@Tag(name = "단어 시험", description = "단어 시험 관련 API")
class WordScoreController(
    private val wordTestService: WordTestService,
    private val wordService: WordService
) {
    @GetMapping("/testLanguageSelect")
    @Operation(summary = "단어 시험 언어 선택", description = "단어 시험 언어를 선택할 때 사용하는 API")
    fun getTestLanguagePage(): String {
        return "word/testLanguage"
    }

    @GetMapping("/testLevelSelect")
    @Operation(summary = "단어 시험 레벨 선택", description = "단어 시험 레벨을 선택할 때 사용하는 API")
    fun getTestLevelPage(@RequestParam lang: String, model: Model): String {
        model.addAttribute("lang", lang)
        return "word/testSeLevelSelect"
    }

    @GetMapping("/start")
    @Operation(summary = "단어 시험 시작", description = "단어 시험을 시작할 때 사용하는 API")
    fun getRandomTestWords(@RequestParam lang: String, @RequestParam level: String): ResponseEntity<Map<String, Any>> {
        val randomTestWords: List<WordResponseDTO> = wordTestService.getRandomTestWords(lang, level)
        val displayTime: Long = if (randomTestWords.isEmpty()) 0 else randomTestWords[0].displayTime

        val testResponse = mapOf(
            "words" to randomTestWords,
            "level" to level,
            "lang" to lang,
            "displayTime" to displayTime
        )

        return ResponseEntity.ok(testResponse)
    }

    @PostMapping("/submit")
    @Operation(summary = "단어 시험 채점", description = "단어 시험 채점할 때 사용하는 API")
    fun submitAnswers(@RequestBody wordScoreRequestDTO: WordScoreRequestDTO): ResponseEntity<Map<String, Any>> {
        val scoreResult = wordTestService.submitAnswer(wordScoreRequestDTO)
        return ResponseEntity.ok(scoreResult)
    }

    @GetMapping("/rankings")
    @Operation(summary = "단어 시험 랭킹 조회", description = "단어 시험 랭킹을 조회할 때 사용하는 API")
    fun getRankings(): List<WordScoreResponseDTO> {
        return wordTestService.getRankings()
    }

    @GetMapping("/records")
    @Operation(summary = "단어 시험 개인 기록 조회", description = "단어 시험 개인 기록을 조회할 때 사용하는 API")
    fun getUserTestScore(@RequestParam memberId: Long): List<WordScoreResponseDTO> {
        return wordTestService.getUserTestScores(memberId)
    }
}