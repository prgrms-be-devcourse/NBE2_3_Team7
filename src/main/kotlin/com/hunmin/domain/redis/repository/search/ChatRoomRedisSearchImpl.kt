package com.hunmin.domain.redis.repository.search

import com.hunmin.domain.redis.entity.ChatRoomRedis
import com.hunmin.domain.redis.repository.ChatRoomRedisRepository

open class ChatRoomRedisSearchImpl(
    private val chatRoomRedisRepository : ChatRoomRedisRepository
){
    fun findByMemberNickname(nickname: String): List<ChatRoomRedis> {

        val chatRooms = mutableListOf<ChatRoomRedis>()
        val redisChatRooms = chatRoomRedisRepository.findAll()

        // 모든 ChatRoomRedis 엔티티를 가져와서 필터링
        for (chatRoom in redisChatRooms) {
            if (chatRoom.member.nickname == nickname) {
                chatRooms.add(chatRoom)
            }
        }
        return chatRooms
    }

    fun findByPartnerNickname(nickname: String): List<ChatRoomRedis> {

        val chatRooms = mutableListOf<ChatRoomRedis>()
        val redisChatRooms = chatRoomRedisRepository.findAll()

        // 모든 ChatRoomRedis 엔티티를 가져와서 필터링
        for (chatRoom in redisChatRooms) {
            if (chatRoom.partner.nickname == nickname) {
                chatRooms.add(chatRoom)
            }
        }
        return chatRooms
    }
}