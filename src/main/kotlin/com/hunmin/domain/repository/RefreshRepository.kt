package com.hunmin.domain.repository

import com.hunmin.domain.entity.RefreshEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface RefreshRepository : JpaRepository<RefreshEntity, Long> {
    fun existsByRefresh(refresh: String): Boolean

    @Transactional
    fun deleteByRefresh(refresh: String)
}