package com.hunmin.domain.controller

import com.hunmin.domain.dto.notification.NotificationResponseDTO
import com.hunmin.domain.entity.Notification
import com.hunmin.domain.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.http.MediaType

@RestController
@RequestMapping("/api/notification")
@Tag(name = "알림", description = "알림 CRUD")
class NotificationController (
    private val notificationService: NotificationService
){
    companion object {
        const val TEXT_EVENT_STREAM = MediaType.TEXT_EVENT_STREAM_VALUE
    }

    // SSE 연결
    @GetMapping(value = ["/subscribe/{memberId}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "sse 연결", description = "sse 연결할 때 사용하는 API")
    fun subscribe(@PathVariable memberId: Long): ResponseEntity<SseEmitter> {
        return ResponseEntity(notificationService.subscribe(memberId), HttpStatus.OK)
    }

    // 회원 별 알림 조회
    @GetMapping("/{memberId}")
    @Operation(summary = "알림 조회", description = "회원 별 알림을 조회할 때 사용하는 API")
    fun readNotifications(@PathVariable memberId: Long): ResponseEntity<List<NotificationResponseDTO>> {
        val notifications: List<Notification> = notificationService.readNotificationByMember(memberId)
        val notificationResponseDTOS: List<NotificationResponseDTO> = notifications.map { NotificationResponseDTO(it) }
        return ResponseEntity(notificationResponseDTOS, HttpStatus.OK)
    }

    // 알림 읽음
    @PutMapping("/{notificationId}")
    @Operation(summary = "알림 읽음", description = "알림을 읽음으로 수정할 때 사용하는 API")
    fun updateNotifications(@PathVariable notificationId: Long): ResponseEntity<NotificationResponseDTO> {
        return ResponseEntity.ok(notificationService.updateNotification(notificationId))
    }
}