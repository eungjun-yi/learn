package org.springframework.data.redis.core

import im.toss.test.equalsTo
import io.lettuce.core.ClientOptions
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.event.DefaultEventPublisherOptions
import io.lettuce.core.resource.DefaultClientResources
import io.lettuce.core.resource.DefaultEventLoopGroupProvider
import io.netty.util.concurrent.DefaultEventExecutorGroup
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializationContext
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import redis.embedded.RedisServer
import java.time.Duration
import java.time.LocalDateTime

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

    @Test
    @DisplayName("Redis command execution via Lettuce connection may be blocked because of other execution")
    fun testRedisCommand() {
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

        ops.put(key, hkey, value).map {
            println("1: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops.put(key, hkey, value).map {
            Thread.sleep(1000)
            println("2: Sleep 1s and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops.put(key, hkey, value).map {
            println("3: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().block()

        redisServer.stop()
    }

    @Test
    fun publishOnAnotherThread() {
        val redisServer = RedisServer(6379)
        redisServer.start()

        val eventExecutorGroup = DefaultEventLoopGroupProvider.createEventLoopGroup(
            DefaultEventExecutorGroup::class.java,
            4
        )
        val clientResources = DefaultClientResources.builder()
            .eventExecutorGroup(eventExecutorGroup)
            .build()
        val lettuceConnectionFactory = LettuceConnectionFactory(
            RedisStandaloneConfiguration(),
            LettuceClientConfiguration.builder()
                .clientResources(clientResources)
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

        ops.put(key, hkey, "a").publishOn(Schedulers.parallel()).map {
            println("1: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops.put(key, hkey, "b").publishOn(Schedulers.parallel()).map {
            Thread.sleep(1000)
            println("2: Sleep 1s and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops.put(key, hkey, "c").publishOn(Schedulers.parallel()).map {
            println("3: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()

        Mono.delay(Duration.ofSeconds(2)).block()

        redisServer.stop()
    }
}
