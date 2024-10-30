package com.hunmin.domain.service

import com.hunmin.domain.dto.notification.NotificationResponseDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.entity.Notification
import com.hunmin.domain.exception.NotificationException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

@Service
@Transactional
class NotificationService(
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository,
    private val sseEmitters: SseEmitters
) {
    companion object {
        private const val TIMEOUT = 5 * 60 * 1000L
    }

    //클라이언트의 연결 구독
    fun subscribe(memberId: Long): SseEmitter {
        val existingId = "${memberId}_"
        val existingEmitters = sseEmitters.findEmitter(existingId)

        existingEmitters.forEach { (key, emitter) ->
            emitter.complete()
            sseEmitters.delete(key)
        }

        val emitter = SseEmitter(TIMEOUT)
        val sseId = "${memberId}_${System.currentTimeMillis()}"

        sseEmitters.create(sseId, emitter)

        val testContent = mapOf("content" to "connected!")
        sendToClient(emitter, sseId, testContent)

        emitter.onTimeout {
            emitter.complete()
            sseEmitters.delete(sseId)
        }

        emitter.onError { throwable ->
            emitter.complete()
            sseEmitters.delete(sseId)
        }

        emitter.onCompletion {
            sseEmitters.delete(sseId)
        }

        return emitter
    }

    //클라이언트에 데이터 전송
    private fun sendToClient(emitter: SseEmitter, sseId: String, data: Any) {
        try {
            emitter.send(
                SseEmitter.event()
                .id(sseId)
                .data(data))
        } catch (exception: IOException) {
            sseEmitters.delete(sseId)
            throw NotificationException.NOT_SEND.toException()
        }
    }

    //알림 전송
    @Transactional
    fun send(notificationSendDTO: NotificationSendDTO) {
        try {
            val member = memberRepository.findById(notificationSendDTO.memberId).orElseThrow()

            val notification = Notification.builder()
                .member(member)
                .message(notificationSendDTO.message)
                .notificationType(notificationSendDTO.notificationType)
                .url(notificationSendDTO.url)
                .isRead(false)
                .build()

            notificationRepository.save(notification)

            val memberId = "${member.memberId}_"
            val emitters = sseEmitters.findEmitter(memberId)
            val notificationResponseDTO = NotificationResponseDTO(notification)

            emitters.forEach { (key, emitter) ->
                sendToClient(emitter, memberId, notificationResponseDTO)
            }
        } catch (e: Exception) {
            throw NotificationException.NOT_SEND.toException()
        }
    }

    //회원 별 알림 조회
    fun readNotificationByMember(memberId: Long): List<Notification> {
        return notificationRepository.findByMemberId(memberId)
    }

    //알림 읽음 처리
    @Transactional
    fun updateNotification(notificationId: Long): NotificationResponseDTO {
        val notification = notificationRepository.findById(notificationId).orElseThrow { NotificationException.NOT_FOUND.toException() }

        return try {
            notification.changeIsRead(true)
            NotificationResponseDTO(notification)
        } catch (e: Exception) {
            throw NotificationException.NOT_UPDATED.toException()
        }
    }
}