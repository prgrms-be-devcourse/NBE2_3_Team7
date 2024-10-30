package com.hunmin.domain.dto.word

import com.hunmin.domain.entity.WordScore
import java.time.LocalDateTime

data class WordScoreResponseDTO (
    val wordScoreId: Long,
    val memberId: Long,
    val nickName: String,
    val profileImage: String? = null,
    val testLang: String,
    val testLevel: String,
    val testScore: Int,
    val testRankScore: Double,
    val createdAt: LocalDateTime
) {
    constructor(wordScore: WordScore) : this(
        wordScoreId = wordScore.wordScoreId,
        memberId = wordScore.member.memberId,
        nickName = wordScore.member.nickname,
        profileImage = wordScore.member?.image,
        testLang = wordScore.testLang,
        testLevel = wordScore.testLevel,
        testScore = wordScore.testScore,
        testRankScore = wordScore.testRankScore,
        createdAt = wordScore.createdAt
    )
}