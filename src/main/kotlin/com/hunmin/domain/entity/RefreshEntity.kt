package com.hunmin.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

// refresh 토큰 저장을 위한 엔티티
@Entity
@Table(name = "refresh_tokens")
data class RefreshEntity (
    @Id
    @Column(nullable = false, unique = true)
    var email: String = "",
    var refresh: String = "",
    var expiration: String = ""
)
