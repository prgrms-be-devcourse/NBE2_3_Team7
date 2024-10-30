package com.hunmin.domain.controller.advice

import com.hunmin.domain.exception.BoardTaskException
import com.hunmin.domain.exception.CommentTaskException
import com.hunmin.domain.exception.NotificationTaskException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class APIControllerAdvice {

    // 게시글 예외 처리
    @ExceptionHandler(BoardTaskException::class)
    fun handleBoardTaskException(e: BoardTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 댓글 예외 처리
    @ExceptionHandler(CommentTaskException::class)
    fun handleCommentTaskException(e: CommentTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 알림 예외 처리
    @ExceptionHandler(NotificationTaskException::class)
    fun handleNotificationTaskException(e: NotificationTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }
}
