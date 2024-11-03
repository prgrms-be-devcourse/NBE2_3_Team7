package com.hunmin.domain.repository

import com.hunmin.domain.entity.WordScore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface WordScoreRepository : JpaRepository<WordScore, Long> {
    // 전체 랭킹
    @Query("SELECT ws FROM WordScore ws ORDER BY ws.testRankScore DESC, ws.testLevel DESC, ws.createdAt ASC")
    fun getTopRankers(): List<WordScore>

    // 개인 시험 기록 조회
    @Query("SELECT ws FROM WordScore ws WHERE ws.member.memberId = :memberId ORDER BY ws.createdAt DESC")
    fun getUserScores(@Param("memberId") memberId: Long): List<WordScore>
}