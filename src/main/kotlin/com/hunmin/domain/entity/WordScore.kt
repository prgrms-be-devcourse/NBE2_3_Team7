package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class WordScore (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val wordScoreId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    var testLang: String? = null,
    var testLevel: String? = null,
    var testScore: Int? = null,
    var testRankScore: Double? = null
) : BaseTimeEntity()