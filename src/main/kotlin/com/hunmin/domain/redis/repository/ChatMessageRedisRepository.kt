package com.hunmin.domain.redis.repository

import com.hunmin.domain.redis.entity.ChatMessageRedis
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRedisRepository: CrudRepository<ChatMessageRedis, Long> {
}