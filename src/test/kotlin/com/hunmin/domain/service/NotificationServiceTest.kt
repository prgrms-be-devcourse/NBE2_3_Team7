package com.hunmin.domain.service

import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.Notification
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.NotificationRepository
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.Optional

class NotificationServiceTest {

    @InjectMocks
    private lateinit var notificationService: NotificationService

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @Mock
    private lateinit var sseEmitters: SseEmitters

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    //SSE 연결 테스트
    @Test
    fun subscribe() {
        val memberId = 1L
        val response = mock(HttpServletResponse::class.java)
        val emitter = notificationService.subscribe(memberId)

        assertNotNull(emitter)
    }

    // 알림 전송 테스트
    @Test
    fun sendNotification() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val sendDTO = NotificationSendDTO(
            notificationId = 1L,
            memberId = 1L,
            message = "알림 테스트 메세지",
            notificationType = NotificationType.COMMENT,
            url = "/board/1"
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))

        notificationService.send(sendDTO)

        verify(notificationRepository, times(1)).save(any(Notification::class.java))
    }

    // 회원 별 알림 조회 테스트
    @Test
    fun readNotificationByMember() {
        val memberId = 1L

        notificationService.readNotificationByMember(memberId)

        verify(notificationRepository, times(1)).findByMemberId(memberId)
    }

    // 알림 읽음 처리 테스트
    @Test
    fun updateNotification() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val notification = Notification.builder()
            .notificationId(1L)
            .member(member)
            .message("알림 테스트 메세지")
            .notificationType(NotificationType.COMMENT)
            .url("/test/url")
            .isRead(false)
            .build()

        `when`(notificationRepository.findById(1L)).thenReturn(Optional.of(notification))

        val responseDTO = notificationService.updateNotification(1L)

        notification.changeIsRead(true)

        assertEquals(true, notification.isRead)
        assertNotNull(responseDTO)
    }
}