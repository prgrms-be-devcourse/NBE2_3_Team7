package com.hunmin.domain.controller

import com.hunmin.domain.dto.chat.ChatRoomDTO
import com.hunmin.domain.dto.chat.ChatRoomRequestDTO
import com.hunmin.domain.service.ChatRoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat-room")
@Tag(name = "채팅목록", description = "채팅목록 CRUD")
class ChatRoomController(private val chatRoomService: ChatRoomService) {

    //채팅방 생성
    @PostMapping("/{nickName}")
    @Operation(summary = "채팅방 생성", description = "채팅방을 이름으로 생성하는 API")
    fun createRoomByNickName(
        @Validated
        @PathVariable("nickName") nickName: String,
        authentication: Authentication
    ): ResponseEntity<ChatRoomRequestDTO> {
        val currentMemberEmail = authentication.name
        return ResponseEntity.ok(
            chatRoomService.createChatRoomByNickName(
                nickName,
                currentMemberEmail
            )
        )
    }

    //나랑 관련된 채팅방만 조회
    @GetMapping("/list")
    @Operation(summary = "채팅방 조회", description = "사용자와 관련된 채팅방 조회하는 API")
    fun myRooms(authentication: Authentication): ResponseEntity<List<ChatRoomRequestDTO>> {
        val currentMemberEmail = authentication.name
        return ResponseEntity.ok(chatRoomService.findRoomByEmail(currentMemberEmail))
    }

    //단일 채팅방 정보 조회
    @GetMapping("/{chatRoomId}")
    @Operation(summary = "단일 채팅방 정보 조회", description = "검색하고 싶은 채팅방을 조회하는 API")
    fun roomInfo(@PathVariable chatRoomId: Long): ResponseEntity<ChatRoomDTO> {
        return ResponseEntity.ok(chatRoomService.findRoomById(chatRoomId))
    }

    //채팅방 삭제
    @DeleteMapping("/{chatRoomId}/{partnerName}")
    @Operation(summary = "채팅방 삭제", description = "삭제하고 싶은 채팅방을 삭제하는 API")
    fun deleteRoom(
        @Validated
        @PathVariable chatRoomId: Long,
        @PathVariable partnerName: String,
        authentication: Authentication
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(chatRoomService.deleteChatRoom(chatRoomId, partnerName, authentication.name))
    }
}
