package com.npcode.learning.reactor
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

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
    fun http2() {
        val client = WebClient.create()
        client.get().uri("https://twitter.com").exchange().map { it.statusCode() }.log().block()
    }
}
