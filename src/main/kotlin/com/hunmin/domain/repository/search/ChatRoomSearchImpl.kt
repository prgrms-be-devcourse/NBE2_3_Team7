package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.chat.ChatRoomRequestDTO
import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.QChatRoom
import com.querydsl.core.types.Projections
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class ChatRoomSearchImpl : QuerydslRepositorySupport(ChatRoom::class.java), ChatRoomSearch {
    override fun findChatRoomByMember(memberId: Long): List<ChatRoomRequestDTO> {
        val chatRoom = QChatRoom.chatRoom

        val query = from(chatRoom)
            .where(chatRoom.member.memberId.eq(memberId))

        val dtoQuery = query.select(
            Projections.constructor(
                ChatRoomRequestDTO::class.java,
                chatRoom.chatRoomId.`as`("chatRoomId"),
                chatRoom.member.memberId.`as`("memberId"),
                chatRoom.member.nickname.`as`("nickName"),
                chatRoom.partner.nickname.`as`("partnerName"),
                chatRoom.createdAt.`as`("createdAt")
            )
        )
        val chatRoomList = dtoQuery.fetch()
        log.info("chatRoomList-member $chatRoomList")

        return chatRoomList
    }

    override fun findChatRoomByPartner(partnerId: Long): List<ChatRoomRequestDTO> {
        val chatRoom = QChatRoom.chatRoom

        val query = from(chatRoom)
            .where(chatRoom.partner.memberId.eq(partnerId))

        val dtoQuery = query.select(
            Projections.constructor(
                ChatRoomRequestDTO::class.java,
                chatRoom.chatRoomId.`as`("chatRoomId"),
                chatRoom.member.memberId.`as`("memberId"),
                chatRoom.member.nickname.`as`("nickName"),
                chatRoom.partner.nickname.`as`("partnerName"),
                chatRoom.createdAt.`as`("createdAt")
            )
        )
        val chatRoomList = dtoQuery.fetch()
        log.info("chatRoomList-partner $chatRoomList")
        return chatRoomList
    }
}