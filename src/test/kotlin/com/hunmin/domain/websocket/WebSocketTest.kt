package com.hunmin.domain.websocket

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.TestPropertySource
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class WebSocketTest {

    private val stompClient: WebSocketStompClient = WebSocketStompClient(StandardWebSocketClient()).apply {
        messageConverter = MappingJackson2MessageConverter()
    }
    private lateinit var session: StompSession
    private lateinit var messageQueue: BlockingQueue<String>

    @BeforeEach
    fun setup() {

        messageQueue = ArrayBlockingQueue(1)
        // WebSocketStompClient 인스턴스 생성
        stompClient.messageConverter = MappingJackson2MessageConverter() // JSON 메시지 전환
        // WebSocket 연결
        session = stompClient.connect("ws://localhost:8080/ws-stomp", object : StompSessionHandlerAdapter() {}).get()
    }

    @Test
    fun 채팅전송() {

        // 메시지 수신 구독 설정
        session.subscribe("/pub/messages", object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = String::class.java
            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                messageQueue.offer(payload as String)
            }
        })

        // 메시지 전송
        val chatMessage = mapOf("sender" to "testUser", "content" to "Hello WebSocket!")
        session.send("/sub/chat.sendMessage", chatMessage)

        // 메시지 수신 및 검증
        val receivedMessage = messageQueue.poll(3, TimeUnit.SECONDS)
        assertEquals("Hello WebSocket!", receivedMessage)
    }

}