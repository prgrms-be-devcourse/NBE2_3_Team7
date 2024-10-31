package com.hunmin.domain.controller.advice

import com.hunmin.domain.exception.BoardTaskException
import com.hunmin.domain.exception.BookmarkTaskException
import com.hunmin.domain.exception.CommentTaskException
import com.hunmin.domain.exception.LikeCommentTaskException
import com.hunmin.domain.exception.NotificationTaskException
import com.hunmin.domain.exception.chat.ChatMessageTaskException
import com.hunmin.domain.exception.chat.ChatRoomTaskException
import com.hunmin.domain.exception.follow.FollowTaskException
import com.hunmin.domain.exception.*
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

    // 북마크 예외 처리
    @ExceptionHandler(BookmarkTaskException::class)
    fun handleBookmarkTaskException(e: BookmarkTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 댓글 좋아요 예외 처리
    @ExceptionHandler(LikeCommentTaskException::class)
    fun handleLikeCommentTaskException(e: LikeCommentTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 알림 예외 처리
    @ExceptionHandler(NotificationTaskException::class)
    fun handleNotificationTaskException(e: NotificationTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 채팅메세지 예외 처리
    @ExceptionHandler(ChatMessageTaskException::class)
    fun handleChatMessageException(e: ChatMessageTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 채팅룸 예외 처리
    @ExceptionHandler(ChatRoomTaskException::class)
    fun handleChatRoomException(e: ChatRoomTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 팔로우 예외 처리
    @ExceptionHandler(FollowTaskException::class)
    fun handleFollowException(e: FollowTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 공지사항 예외 처리
    @ExceptionHandler(NoticeTaskException::class)
    fun handleNoticeTaskException(e: NoticeTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }

    // 관리자 예외 처리
    @ExceptionHandler(AdminTaskException::class)
    fun handleAdminTaskException(e: AdminTaskException): ResponseEntity<Map<String, String>> {
        val map = mapOf("error" to e.message)
        return ResponseEntity.status(e.code).body(map)
    }
}
