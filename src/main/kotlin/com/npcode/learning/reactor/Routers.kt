package com.npcode.learning.reactor

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import reactor.netty.resources.ConnectionProvider

val clientBuilder = WebClient.builder().baseUrl("http://localhost:8000")

val client = clientBuilder.build()

val bigdata = "a".repeat(40960)

@Configuration
class Configuration {
    @Bean
    fun router() = router {
        POST("/post") {
            ServerResponse.ok().body(
                client2()
                    .post()
                    .uri("/post")
                    .body(Mono.just(bigdata))
                    .retrieve()
                    .bodyToMono(String::class.java),
                String::class.java
            )
        }
    }
}

// test1: reuse client
fun client() = client

fun resourceFactory(): ReactorResourceFactory {
    val factory = ReactorResourceFactory()
    factory.connectionProvider = ConnectionProvider.create("custom", 32)
    return factory
}

val fixedPoolClient = clientBuilder.clientConnector(
    ReactorClientHttpConnector(resourceFactory()) { it }
).build()

fun client2() = fixedPoolClient

// test2: reuse client
