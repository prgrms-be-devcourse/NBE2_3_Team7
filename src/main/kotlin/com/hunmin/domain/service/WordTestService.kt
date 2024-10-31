package com.hunmin.domain.service

import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.dto.word.WordScoreRequestDTO
import com.hunmin.domain.dto.word.WordScoreResponseDTO
import com.hunmin.domain.entity.Word
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.WordRepository
import com.hunmin.domain.repository.WordScoreRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WordTestService (
    private val wordRepository: WordRepository,
    private val wordScoreRepository: WordScoreRepository,
    private val memberRepository: MemberRepository
) {
    fun getRandomTestWords(lang: String, level: String): List<WordResponseDTO> {
        val words = wordRepository.findByLang(lang).shuffled()
        val selectedWords = words.take(getQuestionCount(level))

        return selectedWords.map { mapToResponseDTO(it) }
    }

    private fun getQuestionCount(level: String): Int {
        return when (level) {
            "1" -> 25
            "2" -> 50
            "3" -> 100
            else -> throw IllegalArgumentException("Invalid level: $level")
        }
    }

    private fun mapToResponseDTO(word: Word): WordResponseDTO {
        return WordResponseDTO(word).apply {
            displayTitle = translation
            displayTranslation = title
            definition = word.definition
        }
    }

    @Transactional
    fun submitAnswer(requestDTO: WordScoreRequestDTO): Map<String, Any> {
        val finalScore = calculateFinalScore(requestDTO.correctCount, requestDTO.testLevel)
        val penaltyScore = calculatePenaltyScore(finalScore, requestDTO.testLevel)

        requestDTO.setScore(finalScore, penaltyScore)
        saveTestScore(requestDTO)

        return mapOf("finalScore" to finalScore, "penaltyScore" to penaltyScore)
    }

    @Transactional
    fun saveTestScore(requestDTO: WordScoreRequestDTO) {
        val member = memberRepository.findById(requestDTO.memberId)
            .orElseThrow { RuntimeException("Member not found") }

        val wordScore = requestDTO.toEntity(member)
        wordScoreRepository.save(wordScore)
    }

    // 정답 개수 * 레벨에 해당되는 문제 하나당 점수
    private fun calculateFinalScore(correctCount: Int, testLevel: String): Int {
        val pointsPerCorrect = getPoint(testLevel)
        return correctCount * pointsPerCorrect
    }

    // 최종 점수에 레벨에 해당되는 페널티 부여
    fun calculatePenaltyScore(finalScore: Int, testLevel: String): Double {
        val penaltyPercent = getPenalty(testLevel)
        return finalScore * (1 - penaltyPercent)
    }

    // 레벨에 따른 점수 반환
    fun getPoint(testLevel: String): Int {
        return when (testLevel) {
            "1" -> 4 // 난이도 하
            "2" -> 2 // 난이도 중
            "3" -> 1 // 난이도 상
            else -> 0 // 잘못된 레벨
        }
    }

    // 레벨에 따른 패널티 점수
    fun getPenalty(testLevel: String): Double {
        return when (testLevel) {
            "1" -> 0.20
            "2" -> 0.10
            "3" -> 0.0
            else -> throw IllegalArgumentException("Invalid level: $testLevel")
        }
    }

    // 전체 랭킹 순위
    fun getRankings(): List<WordScoreResponseDTO> {
        val wordScores = wordScoreRepository.getTopRankers()

        // 랭킹 기록이 없을 경우 빈 리스트 반환
        if (wordScores.isEmpty()) return emptyList()

        // WordScore를 WordScoreResponseDTO로 변환
        return wordScores.map { WordScoreResponseDTO(it) }
    }

    // 개인 시험 기록
    fun getUserTestScores(memberId: Long): List<WordScoreResponseDTO> {
        val wordScores = wordScoreRepository.getUserScores(memberId)

        // 시험 기록이 없을 경우 빈 리스트 반환
        if (wordScores.isEmpty()) return emptyList()

        // WordScore를 WordScoreResponseDTO로 변환
        return wordScores.map { WordScoreResponseDTO(it) }
    }
}