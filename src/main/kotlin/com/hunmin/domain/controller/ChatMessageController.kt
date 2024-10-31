package com.hunmin.domain.controller

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.dto.chat.ChatMessageListRequestDTO
import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.service.ChatMessageService
import com.hunmin.domain.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.hibernate.query.Page.page
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/chat")
@Tag(name = "채팅", description = "채팅 CRUD")
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val memberService: MemberService
) {
    private val logger = KotlinLogging.logger {}

    //클라이언트로 부터 오는 메세지 수신 -> Redis로 송신
    @MessageMapping("/api/chat/message")
    fun sendMessage(@Validated message: ChatMessageDTO) {
        chatMessageService.sendChatMessage(message)
    }

    //단일 채팅 조회
    @GetMapping("/{chatMessageId}")
    @ResponseBody
    @Operation(summary = "채팅 검색", description = "검색하고 싶은 채팅을 조회하는 API")
    fun readMessage(@Validated @PathVariable chatMessageId: Long): ResponseEntity<ChatMessageDTO> {
        logger.info { "Long {}" + chatMessageId }
        return ResponseEntity.ok(chatMessageService.readChatMessage(chatMessageId))
    }

    //채팅 수정
    @PutMapping("/{message}")//프론트 화면에 수정 필요! put GetParam -> pathvariable
    @ResponseBody
    @Operation(summary = "채팅 수정", description = "채팅 내역을 수정하는 API")
    fun updateMessage(@Validated @PathVariable message: ChatMessageDTO): ResponseEntity<ChatMessageDTO> {
        return ResponseEntity.ok(chatMessageService.updateChatMessage(message))
    }

    //채팅삭제
    @DeleteMapping("/{chatMessageId}")
    @ResponseBody
    @Operation(summary = "채팅 삭제", description = "삭제하고 싶은 채팅을 삭제하는 API")
    fun deleteMessage(@Validated @PathVariable chatMessageId: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(chatMessageService.deleteChatMessage(chatMessageId))
    }

    //사용자 정보 호출
    @GetMapping("/user-info")
    @ResponseBody
    @Operation(summary = "사용자 정보", description = "사용자 정보 호출하는 API")
    fun getUserInfo(authentication: Authentication): ResponseEntity<MemberDTO> {
        val email = authentication.name
        return ResponseEntity.ok(memberService.readUserInfo(email))

    }

    //페이징 채팅 기록 조회
    @GetMapping("/messages/{chatRoomId}")
    @ResponseBody
    @Operation(summary = "채팅 기록 조회", description = "채팅 기록 호출 API")
    fun loadMessageList(
        @Validated
        @PathVariable chatRoomId: Long,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<Page<ChatMessageListRequestDTO>> {
        val pageRequestDTO = PageRequestDTO(page = page,
            size = size)
        return ResponseEntity.ok(
            chatMessageService.getList(pageRequestDTO, chatRoomId)
        )
    }

}
