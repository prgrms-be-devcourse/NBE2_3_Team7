package com.hunmin.domain.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer

//로컬 환경일경우 내장 레디스가 실행된다.
@Profile("local")
@Configuration
class EmbeddedRedisConfig {
    @Value("\${spring.data.redis.port}")
    val redisPort = 0

    var redisServer: RedisServer? = null

    @PostConstruct
    fun redisServer() {
        redisServer = RedisServer(redisPort)
        redisServer!!.start()
    }

    @PreDestroy
    fun stopRedis() {
        if (redisServer != null) {
            redisServer!!.stop()
        }
    }
}
