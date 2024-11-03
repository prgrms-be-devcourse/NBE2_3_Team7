package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.chat.ChatMessageListRequestDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ChatMessageSearch {
    fun getChatMessageList(chatRoomId: Long): List<ChatMessageListRequestDTO>
    fun chatMessageList(pageable: Pageable, chatRoomId: Long): Page<ChatMessageListRequestDTO>
}
