package com.hunmin.domain.repository

import com.hunmin.domain.entity.Notice
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<Notice, Long> {
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.member ")
    fun findAllNotices(pageable: Pageable): Page<Notice>
}