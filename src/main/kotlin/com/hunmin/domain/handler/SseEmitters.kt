package com.hunmin.domain.handler

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Component
class SseEmitters {
    private val emitters: ConcurrentHashMap<String, SseEmitter> = ConcurrentHashMap()

    // SseEmitter 생성
    fun create(sseId: String, emitter: SseEmitter): SseEmitter {
        emitters[sseId] = emitter
        return emitter
    }

    // SseEmitter 찾기
    fun findSingleEmitter(sseId: String): SseEmitter? {
        return emitters[sseId]
    }

    // sseId 별 SseEmitter 찾기
    fun findEmitter(sseId: String): Map<String, SseEmitter> {
        return emitters.entries
            .filter { entry -> entry.key.startsWith(sseId) }
            .associate { entry -> entry.key to entry.value }
    }

    // SseEmitter 삭제
    fun delete(sseId: String) {
        emitters.remove(sseId)
    }
}