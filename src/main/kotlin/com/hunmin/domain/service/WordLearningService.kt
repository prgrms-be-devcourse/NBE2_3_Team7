package com.hunmin.domain.service

import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.entity.Word
import com.hunmin.domain.repository.WordRepository
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class WordLearningService(
    private val wordRepository: WordRepository
) {
    fun getRandomWord(lang: String, level: String): List<WordResponseDTO> {
        val words: List<Word> = wordRepository.findByLang(lang)

        val selectedWords = words.shuffled().take(30)

        return selectedWords.map { word ->
            val wordResponseDTO = WordResponseDTO(word).apply {
                if (Random.nextBoolean()) {
                    displayTitle = title
                    displayTranslation = translation
                } else {
                    displayTitle = translation
                    displayTranslation = title
                }
                displayTime = getDisplayTime(level)
            }
            wordResponseDTO
        }
    }

    private fun getDisplayTime(level: String): Long {
        return when (level) {
            "1" -> 30000
            "2" -> 20000
            "3" -> 10000
            else -> throw IllegalArgumentException("Invalid level: $level")
        }
    }
}