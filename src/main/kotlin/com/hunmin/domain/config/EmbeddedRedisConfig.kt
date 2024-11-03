package com.hunmin.domain.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import java.io.IOException
import java.net.Socket

// 로컬 환경일 경우 내장 레디스가 실행된다.
@Profile("local")
@Configuration
class EmbeddedRedisConfig(
    @Value("\${spring.data.redis.port}")
    private val redisPort: Int
) {

    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedisServer() {
        if (!isRedisRunning()) {
            // Redis가 실행 중이 아니면 임베디드 Redis 서버 시작
            redisServer = RedisServer(redisPort)
            redisServer.start()
            println("임베디드 Redis 서버가 시작되었습니다.")
        } else {
            println("Redis가 이미 실행 중입니다.")
        }
    }

    @PreDestroy
    fun stopRedisServer() {
        if (::redisServer.isInitialized && redisServer.isActive) {
            redisServer.stop()
            println("임베디드 Redis 서버가 중지되었습니다.")
        }
    }

    // Redis 서버가 실행 중인지 확인하는 메서드
    private fun isRedisRunning(): Boolean {
        return try {
            Socket("localhost", redisPort).use { socket -> true }
        } catch (e: IOException) {
            false
        }
    }
}
