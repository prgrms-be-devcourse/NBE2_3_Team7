package com.hunmin.domain.repository

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.Notification
import com.hunmin.domain.entity.NotificationType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class NotificationRepositoryTest {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    //알림 등록 테스트
    @Test
    @Commit
    fun testCreateNotification() {
        val notification = Notification.builder()
            .member(memberRepository.findById(1).get())
            .message("알림 메세지")
            .notificationType(NotificationType.COMMENT)
            .isRead(false)
            .url("/board/1")
            .build()

        val savedNotification = notificationRepository.save(notification)

        assertNotNull(savedNotification)
    }

    //알림 조회 테스트
    @Test
    fun testNotificationRead() {
        val notificationId = 1L

        val notification = notificationRepository.findById(notificationId).orElseThrow()

        assertNotNull(notification)
    }

    //알림 수정 테스트
    @Test
    @Transactional
    @Commit
    fun testUpdateNotification() {
        val notificaitonId = 1L
        val isRead = true;

        val notification = notificationRepository.findById(notificaitonId).orElseThrow()

        notification.changeIsRead(isRead)

        val updatedNotification = notificationRepository.findById(notificaitonId).orElseThrow()

        assertEquals(isRead, updatedNotification.isRead)
    }

    //알림 삭제 테스트
    @Test
    @Transactional
    @Commit
    fun testDeleteNotification() {
        val notificaitonId = 1L

        notificationRepository.deleteById(notificaitonId)

        assertTrue(notificationRepository.findById(notificaitonId).isEmpty)
    }

    //회원 별 알림 조회
    @Test
    fun testReadNotificationByMember() {
        val member: Member = memberRepository.findById(1L).get()

        val notifications: List<Notification> = notificationRepository.findByMemberId(member.memberId)

        assertNotNull(notifications);
    }
}