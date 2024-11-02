package com.hunmin.domain.controller

import com.hunmin.domain.dto.chat.ChatRoomDTO
import com.hunmin.domain.dto.chat.ChatRoomRequestDTO
import com.hunmin.domain.redis.service.ChatRoomRedisService
import com.hunmin.domain.service.ChatRoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat-room")
@Tag(name = "채팅목록", description = "채팅목록 CRUD")
class ChatRoomController(
    private val chatRoomService: ChatRoomService,
    private val chatRoomRedisService: ChatRoomRedisService
) {
    //채팅방 생성 레디스
    @PostMapping("/{nickName}")
    @Operation(summary = "채팅방 생성", description = "채팅방을 이름으로 생성하는 API")
    fun createRoomByNickName(
        @Validated
        @PathVariable("nickName") nickName: String,
        authentication: Authentication
    ): ResponseEntity<ChatRoomRequestDTO> {

        val currentMemberEmail = authentication.name
        log.info("currentMemberEmail is $currentMemberEmail, nickName is $nickName")
        //데이터 베이스로 채팅방 생성
//        val returnValue = chatRoomService.createChatRoomByNickName(nickName, currentMemberEmail)
        val returnValue = chatRoomRedisService.createChatRoomByNickName(nickName, currentMemberEmail)
        return ResponseEntity.ok(returnValue)
    }

    //나랑 관련된 채팅방만 조회
    @GetMapping("/list")
    @Operation(summary = "채팅방 조회", description = "사용자와 관련된 채팅방 조회하는 API")
    fun myRooms(authentication: Authentication): ResponseEntity<List<ChatRoomRequestDTO>> {
        val currentMemberEmail = authentication.name
        println("authentication: $authentication")
        return ResponseEntity.ok(chatRoomService.findRoomByEmail(currentMemberEmail))
    }

    //채팅방 삭제
    @DeleteMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 삭제", description = "삭제하고 싶은 채팅방을 삭제하는 API")
    fun deleteRoom(
        @Validated
        @PathVariable chatRoomId: Long
    ): ResponseEntity<Boolean> {
//        return ResponseEntity.ok(chatRoomService.deleteChatRoom(chatRoomId, partnerName, authentication.name))
        return ResponseEntity.ok(chatRoomRedisService.deleteChatRoom(chatRoomId))

    }
}
