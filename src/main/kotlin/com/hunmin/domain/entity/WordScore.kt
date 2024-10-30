package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class WordScore (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val wordScoreId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    var testLang: String,
    var testLevel: String,
    var testScore: Int,
    var testRankScore: Double
) : BaseTimeEntity()