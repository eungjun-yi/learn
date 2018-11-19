package com.npcode.learning.reactor

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import redis.embedded.RedisServer
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
class RedisConfig {

    private val redisPort = 6379

    private val redisServer = RedisServer(redisPort)

    @PostConstruct
    fun startRedis() {
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }

    // bean으로 안하면 안됨. afterPropertiesSet()이 안불린다
    @Bean
    fun redisConnectionFactory() = LettuceConnectionFactory("localhost", redisPort)

    @Bean
    fun reactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, String> = ReactiveRedisTemplate(factory, RedisSerializationContext.string())
}

