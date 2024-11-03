package com.hunmin.domain.handler

import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

class WebSocketEventListener {
    private val logger = KotlinLogging.logger {}

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        logger.info("Received a new web socket connection")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        logger.info("Web socket connection disconnected")
        logger.error("Disconnect reason: ${event.closeStatus}")
    }
}
