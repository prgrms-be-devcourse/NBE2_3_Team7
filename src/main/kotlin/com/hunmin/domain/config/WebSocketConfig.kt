package com.hunmin.domain.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
//    private val stompHandler: StompHandler? = null

    //stomp로 pub/sub 주소 생성
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub")
        registry.setApplicationDestinationPrefixes("/pub")
    }

    //endPoint 설정
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws-stomp")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

//    override fun configureClientInboundChannel(registration: ChannelRegistration) {
//        registration.interceptors(stompHandler)
//    }

    @Bean
    fun mappingJackson2MessageConverter(): MappingJackson2MessageConverter {
        val jackson2MessageConverter: MappingJackson2MessageConverter = MappingJackson2MessageConverter()
        jackson2MessageConverter.setStrictContentTypeMatch(false)
        return jackson2MessageConverter
    }
}
