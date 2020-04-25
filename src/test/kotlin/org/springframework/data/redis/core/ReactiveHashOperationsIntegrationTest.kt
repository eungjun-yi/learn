package org.springframework.data.redis.core

import im.toss.test.equalsTo
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializationContext
import redis.embedded.RedisServer

class ReactiveHashOperationsIntegrationTest {

    @Test
    fun test() {
        val redisServer = RedisServer(6379)
        redisServer.start()

        val lettuceConnectionFactory = LettuceConnectionFactory()
        val redisTemplate = ReactiveRedisTemplate(
            lettuceConnectionFactory,
            RedisSerializationContext.string()
        )
        lettuceConnectionFactory.afterPropertiesSet()

        val ops = redisTemplate.opsForHash<String, String>()

        val key = "person"
        val hkey = "name"
        val value = "a"

        ops.put(key, hkey, value).block().equalsTo(true)
        ops.put(key, hkey, value).block().equalsTo(false)

        redisServer.stop()
    }
}
