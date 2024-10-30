package com.hunmin.domain.repository

import com.hunmin.domain.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NotificationRepository : JpaRepository<Notification, Long> {

    //회원 별 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.member.memberId = :memberId ORDER BY n.notificationId DESC")
    fun findByMemberId(@Param("memberId") memberId: Long): List<Notification>
}