package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.chat.ChatMessageListRequestDTO
import com.hunmin.domain.entity.ChatMessage
import com.hunmin.domain.entity.QChatMessage
import com.hunmin.domain.entity.QChatRoom
import com.querydsl.core.types.Projections
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class ChatMessageSearchImpl : QuerydslRepositorySupport(ChatMessage::class.java), ChatMessageSearch {

    override fun chatMessageList(pageable: Pageable, chatRoomId: Long): Page<ChatMessageListRequestDTO> {
        val chatMessage = QChatMessage.chatMessage
        val chatRoom = QChatRoom.chatRoom

        val query = from(chatMessage)
            .leftJoin(chatMessage.chatRoom, chatRoom)
            .where(chatRoom.chatRoomId.eq(chatRoomId))

        val dtoQuery = query.select(
            Projections.constructor(
                ChatMessageListRequestDTO::class.java,
                chatMessage.chatMessageId,
                chatMessage.member.memberId,
                chatMessage.message,
                chatMessage.createdAt,
                chatMessage.type
            )
        )

        querydsl.applyPagination(pageable, dtoQuery)
        val chatMessageList = dtoQuery.fetch()
        val count = dtoQuery.fetchCount()

        return PageImpl(chatMessageList, pageable, count)
    }
}
