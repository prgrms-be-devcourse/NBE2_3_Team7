package com.hunmin.domain.dto.word

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.WordScore

data class WordScoreRequestDTO (
    val wordScoreId: Long,
    val memberId: Long,
    val testLang: String,
    val testLevel: String,
    var testScore: Int,
    var testRankScore: Double,
    val correctCount: Int
) {
    fun toEntity(member: Member): WordScore = WordScore(
        wordScoreId = wordScoreId,
        member = member,
        testLang = testLang,
        testLevel = testLevel,
        testScore = testScore,
        testRankScore = testRankScore
    )

    fun setScore(finalScore: Int, penaltyScore: Double) {
        this.testScore = finalScore
        this.testRankScore = penaltyScore
    }
}