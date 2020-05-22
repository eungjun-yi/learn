package com.npcode.learning.reactor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier

@SpringBootTest
@RunWith(SpringRunner::class)
class RedisTest {

    @Autowired
    lateinit var redisConnectionFactory: ReactiveRedisConnectionFactory

    @Test
    fun test() {
        val redisTemplate =
            ReactiveRedisTemplate(redisConnectionFactory, RedisSerializationContext.string())

        // When
        redisTemplate.opsForValue().set("name", "apple").block()
        val name = redisTemplate.opsForValue().get("name")

        // Then
        StepVerifier
            .create(name)
            .expectNext("apple")
            .verifyComplete()
    }

    @Test
    fun value() {
        // Given
        val redisTemplate = redisTemplate()
        val apple = Product(1, 1, "fruit", "apple")

        // When
        redisTemplate.opsForValue().set(apple.id.toString(), apple).block()
        val actual = redisTemplate.opsForValue().get(apple.id.toString())

        // Then
        StepVerifier
            .create(actual)
            .expectNext(apple)
            .verifyComplete()
    }

    private fun redisTemplate(): ReactiveRedisTemplate<String, Product> {
        val jackson2JsonRedisSerializer =
            Jackson2JsonRedisSerializer(Product::class.java)
        jackson2JsonRedisSerializer.setObjectMapper(ObjectMapper().registerKotlinModule())
        val context = RedisSerializationContext
            .newSerializationContext<String, Product>(StringRedisSerializer())
            .value(jackson2JsonRedisSerializer)
            .build()
        val redisTemplate = ReactiveRedisTemplate(
            redisConnectionFactory, context
        )
        return redisTemplate
    }

    @Test
    fun list() {
        // Given
        val redisTemplate = redisTemplate()
        val apple = Product(1, 1, "fruit", "apple")
        val strawberry = Product(2, 1, "fruit", "strawberry")
        val peach = Product(3, 2, "fruit", "peach")
        val wheat = Product(4, 3, "grain", "wheat")
        listOf(apple, strawberry, peach, wheat).forEach {
            redisTemplate.opsForList().rightPush(it.marketId.toString(), it).block()
        }

        // When
        val actual = redisTemplate.opsForList().range(1.toString(), 0, 100)

        // Then
        StepVerifier
            .create(actual)
            .expectNext(apple)
            .expectNext(strawberry)
            .verifyComplete()
    }
}

data class Product(
    val id: Long,
    val marketId: Long,
    val type: String,
    val name: String
)
