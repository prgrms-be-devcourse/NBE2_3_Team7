package com.hunmin.domain.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hunmin.domain.redis.entity.ChatRoomRedis
import com.hunmin.domain.redis.sendMessage.RedisSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun topicPattern(): ChannelTopic {
        return ChannelTopic("chatRoom")
    }

    //클라이언트로 부터 메세지 수신
    @Bean
    fun redisMessageListener(
        connectionFactory: RedisConnectionFactory,
        listenerAdapter: MessageListenerAdapter,
        channelTopic: ChannelTopic
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(listenerAdapter, channelTopic)
        return container
    }

    //클라이언트로 부터 메세지 수신
    @Bean
    fun listenerAdapter(subscriber: RedisSubscriber): MessageListenerAdapter {
        return MessageListenerAdapter(subscriber, "sendMessage")
    }

    @Bean(name = ["redisTemplate"])
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(connectionFactory)

        // GenericJackson2JsonRedisSerializer 설정
        val jackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        // Key Serializer 설정 - StringRedisSerializer
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        // Value Serializer 설정
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashValueSerializer = jackson2JsonRedisSerializer

        template.afterPropertiesSet()
        return template
    }

    @Bean(name = ["roomStorage"])
    fun hashOperations(redisTemplate: RedisTemplate<String, Any>): HashOperations<String, String, Any> {
        return redisTemplate.opsForHash()
    }
}
