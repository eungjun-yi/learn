package org.springframework.data.redis.core

import im.toss.test.equalsTo
import io.lettuce.core.ClientOptions
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.event.DefaultEventPublisherOptions
import io.lettuce.core.resource.DefaultClientResources
import io.lettuce.core.resource.DefaultEventLoopGroupProvider
import io.netty.util.concurrent.DefaultEventExecutorGroup
import org.junit.Ignore
import org.junit.jupiter.api.Disabled
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

@Disabled
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
    fun testRedisCommandWithDefaultEndpoint() {
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

    /**
     * 타임아웃 처리는 EventLoop가 아닌 ExecutorLoop 에서
     */
    @Test
    fun doWithCommandExpiryWriter() {
        val redisServer = RedisServer(6379)
        redisServer.start()

        val clientResources = DefaultClientResources.builder().build()
        // 오히려 이게 중요한 것 같다
        val clientOptions = ClientOptions.builder().timeoutOptions(TimeoutOptions.enabled()).build()
        // val clientOptions = ClientOptions.builder().build()
        val lettuceConnectionFactory = LettuceConnectionFactory(
            RedisStandaloneConfiguration(),
            LettuceClientConfiguration.builder()
                .clientResources(clientResources)
                .clientOptions(clientOptions)
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

        ops.put(key, hkey, "a").map {
            println("1: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops.put(key, hkey, "b").map {
            Thread.sleep(1000)
            println("2: Sleep 1s and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops.put(key, hkey, "c").map {
            println("3: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()

        Mono.delay(Duration.ofSeconds(2)).block()

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

    @Test
    fun testMultipleConnection() {
        val redisServer1 = RedisServer(6379)
        val redisServer2 = RedisServer(6380)
        redisServer1.start()
        redisServer2.start()

        val eventExecutorGroup = DefaultEventLoopGroupProvider.createEventLoopGroup(
            DefaultEventExecutorGroup::class.java,
            2
        )
        val clientResources = DefaultClientResources.builder()
            .eventExecutorGroup(eventExecutorGroup)
            .build()

        val lettuceConnectionFactory1 = LettuceConnectionFactory(
            RedisStandaloneConfiguration("localhost", 6379),
            LettuceClientConfiguration.builder().clientResources(clientResources).build()
        )
        val redisTemplate1 = ReactiveRedisTemplate(
            lettuceConnectionFactory1,
            RedisSerializationContext.string()
        )
        lettuceConnectionFactory1.afterPropertiesSet()

        val lettuceConnectionFactory2 = LettuceConnectionFactory(
            RedisStandaloneConfiguration("localhost", 6379),
            LettuceClientConfiguration.builder().clientResources(clientResources).build()
        )
        val redisTemplate2 = ReactiveRedisTemplate(
            lettuceConnectionFactory2,
            RedisSerializationContext.string()
        )
        lettuceConnectionFactory2.afterPropertiesSet()

        val ops1 = redisTemplate1.opsForHash<String, String>()
        val ops2 = redisTemplate2.opsForHash<String, String>()

        val key = "person"
        val hkey = "name"
        val value = "a"

        ops1.put(key, hkey, value).map {
            println("1: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops1.put(key, hkey, value).map {
            Thread.sleep(1000)
            println("2: Sleep 1s and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops1.put(key, hkey, value).map {
            println("3: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops2.put(key, hkey, value).map {
            println("4: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()

        Mono.delay(Duration.ofSeconds(2)).block()

        redisServer1.stop()
        redisServer2.stop()
    }

    /**
     * eventLoopGroupProvider 가 single thread 면 connection이 여러개라도 하나의 event loop로 처리한다.
     *
     * eventExecutorGroup, computationPoolSize, ioPoolSize 전부 아님
     */
    @Test
    fun testSingleThreadAndMultipleConnection() {
        val redisServer1 = RedisServer(6379)
        val redisServer2 = RedisServer(6380)
        redisServer1.start()
        redisServer2.start()

        val clientOptions = ClientOptions.builder().build()
        val clientResources = DefaultClientResources.builder()
            .eventExecutorGroup(DefaultEventExecutorGroup(4))
            .eventLoopGroupProvider(DefaultEventLoopGroupProvider(8))
            .computationThreadPoolSize(16)
            .ioThreadPoolSize(32)
            .build()
        val clientConfig = LettuceClientConfiguration.builder()
            .clientResources(clientResources)
            .clientOptions(clientOptions)
            .build()

        val lettuceConnectionFactory1 = LettuceConnectionFactory(
            RedisStandaloneConfiguration("localhost", 6379),
            clientConfig
        )
        val redisTemplate1 = ReactiveRedisTemplate(
            lettuceConnectionFactory1,
            RedisSerializationContext.string()
        )
        lettuceConnectionFactory1.afterPropertiesSet()

        val lettuceConnectionFactory2 = LettuceConnectionFactory(
            RedisStandaloneConfiguration("localhost", 6380),
            clientConfig
        )
        val redisTemplate2 = ReactiveRedisTemplate(
            lettuceConnectionFactory2,
            RedisSerializationContext.string()
        )
        lettuceConnectionFactory2.afterPropertiesSet()

        val ops1 = redisTemplate1.opsForHash<String, String>()
        val ops2 = redisTemplate2.opsForHash<String, String>()

        val key = "person"
        val hkey = "name"
        val value = "a"

        ops1.put(key, hkey, value).map {
            println("1: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops1.put(key, hkey, value).map {
            Thread.sleep(1000)
            println("2: Sleep 1s and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops1.put(key, hkey, value).map {
            println("3: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()
        ops2.put(key, hkey, value).map {
            println("4: Don't sleep and print | now= ${LocalDateTime.now()} thread= ${Thread.currentThread()}")
        }.log().subscribe()

        Mono.delay(Duration.ofSeconds(2)).block()

        redisServer1.stop()
        redisServer2.stop()
    }
}
