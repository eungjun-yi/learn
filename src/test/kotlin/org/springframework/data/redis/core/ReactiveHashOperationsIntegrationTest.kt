package org.springframework.data.redis.core

import im.toss.test.equalsTo
import io.lettuce.core.ClientOptions
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.event.DefaultEventPublisherOptions
import io.lettuce.core.resource.DefaultClientResources
import io.lettuce.core.tracing.BraveTracing
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializationContext
import redis.embedded.RedisServer
import java.time.Duration

class ReactiveHashOperationsIntegrationTest {

    @Test
    @DisplayName("ReactiveHashOperations.put() returns true only if the value is created")
    fun testReactiveHashOperationsPut() {
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
        ops.put(key, hkey, value).block().equalsTo(false)

        redisServer.stop()
    }

    @Test
    @DisplayName("do not reconnect")
    fun testReactiveCommandTimeout() {
        val redisServer = RedisServer(6379)
        redisServer.start()

        val clientResources = DefaultClientResources.builder()
            .commandLatencyPublisherOptions(
                DefaultEventPublisherOptions.builder()
                    .eventEmitInterval(Duration.ofMinutes(1))
                    .build()
            )
            .build()

        val lettuceConnectionFactory = LettuceConnectionFactory(
            RedisStandaloneConfiguration(),
            LettuceClientConfiguration.builder()
                .clientResources(clientResources)
                .clientOptions(
                    ClientOptions.builder()
                        .autoReconnect(true)
                        .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(100)))
                        .build()
                )
                .build()
        )
        val redisTemplate = ReactiveRedisTemplate(
            lettuceConnectionFactory,
            RedisSerializationContext.string()
        )
        lettuceConnectionFactory.afterPropertiesSet()

        val ops = redisTemplate.opsForHash<String, String>()

        val key = "person"
        val hkey = "name"
        val value = "a"

        ops.put(key, hkey, value).block()

        redisServer.stop()

        ops.put(key, hkey, value).block()
    }
}
