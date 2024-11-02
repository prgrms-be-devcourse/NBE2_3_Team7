package com.hunmin.domain.redis.repository

import com.hunmin.domain.redis.entity.ChatRoomRedis
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRedisRepository: CrudRepository<ChatRoomRedis, Long> {
    fun findByMemberNickname(nickname: String): List<ChatRoomRedis>
}