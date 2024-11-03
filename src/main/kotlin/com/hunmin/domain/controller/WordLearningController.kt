package com.hunmin.domain.controller

import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.service.WordLearningService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/api/words/learning")
@Tag(name = "단어 학습", description = "단어 학습 관련 API")
class WordLearningController (
    private val wordLearningService: WordLearningService
) {
    @GetMapping("/languageSelect")
    @Operation(summary = "단어 학습 언어 선택", description = "단어 학습 언어를 선택할 때 사용하는 API")
    fun getLearningLanguagePage(): String {
        return "word/LearningLanguage";
    }

    @GetMapping("/levelSelect")
    @Operation(summary = "단어 학습 레벨 선택", description = "단어 학습 레벨을 선택할 때 사용하는 API")
    fun levelPage(@RequestParam lang: String, model : Model): String {
        model.addAttribute("lang", lang)
        return "word/levelSelect"
    }

    @GetMapping("start")
    @ResponseBody
    @Operation(summary = "단어 학습 시작", description = "단어 학습을 시작할 때 사용하는 API")
    fun getRandomWords(@RequestParam lang: String, @RequestParam level: String): ResponseEntity<Map<String, Any>> {
        val randomWords: List<WordResponseDTO> = wordLearningService.getRandomWord(lang, level)

        val displayTime: Long = randomWords.firstOrNull()?.displayTime ?: 0L

        val learningResponse = mapOf(
            "words" to randomWords,
            "level" to level,
            "lang" to lang,
            "displayTime" to displayTime
        )
        return ResponseEntity.ok(learningResponse)
    }
}