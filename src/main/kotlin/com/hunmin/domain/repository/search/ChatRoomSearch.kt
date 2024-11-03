package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.chat.ChatRoomRequestDTO

interface ChatRoomSearch {
    fun findChatRoomByMember(memberId: Long): List<ChatRoomRequestDTO>
    fun findChatRoomByPartner(partnerId: Long): List<ChatRoomRequestDTO>
}