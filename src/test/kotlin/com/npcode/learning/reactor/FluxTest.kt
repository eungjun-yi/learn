package com.npcode.learning.reactor
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

class WebClientTest {
    @Test
    fun testErrorHandling() {
        val client = WebClient.create()

        // 404 not found
        val html = client.get()
                .uri("http://example.org/foo")
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .bodyToFlux(String::class.java)
                .filter { it != null }
                .map { it }
                .collectList()
                .onErrorReturn(emptyList())
                .block()

        System.out.println(html)
    }

    @Test
    fun concatMultipleFlux() {
        val a = Flux.fromIterable(listOf(1, 3, 5))
        val b = Flux.fromIterable(listOf(2, 4, 8))

        a.concatWith(b).map { System.out.println(it) }
    }
}
