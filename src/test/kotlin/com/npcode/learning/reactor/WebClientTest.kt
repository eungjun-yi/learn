package com.npcode.learning.reactor
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.test

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

    @Test
    fun connectionClosedPrematurelyByServer() {
        // nc -l -p 8000 해서 8000번 포트 열어놓고 테스트 실행

        // GET http://localhost:8000/foo 로 요청을 보내서 응답으로 "test"를 기대한다.
        val client = WebClient.create()
        val body = client
            .get()
            .uri("http://localhost:8000/foo")
            .exchange()
            .map { it.bodyToMono(String::class.java) }
            .flatMap { it }
            .log()
            .test()
            .expectNext("test")
            .verifyComplete()

        // nc로 GET /foo 요청이 올 것이다.
        // 클라이언트는 "test"가 오길 기대하겠지만 무시하고 그냥 nc를 끄면 이런 에러 발생:
        // reactor.netty.http.client.PrematureCloseException: Connection prematurely closed BEFORE response

        Thread.sleep(10000)
    }

    @Test
    fun connectionClosedPrematurelyByClient() {
        // nc -l -p 8000 해서 8000번 포트 열어놓고 테스트 실행

        // GET http://localhost:8000/foo 로 요청을 보낸다.
        val client = WebClient.create()
        val body = client
            .get()
            .uri("http://localhost:8000/foo")
            .exchange()
            .map { it.bodyToMono(String::class.java) }
            .block()

        // nc로 GET /foo 요청이 올 것이다.
        // 본문 없이 헤더까지만 응답해주면 여기까지 온다.
        System.out.println("waiting...")

        // 서버가 HTTP 메시지 전송을 완료했지만, 클라이언트가 body를 subscribe 하지는 않은 이 시점에서 서버가 커넥션을 끊으면?
        // HTTP 메시지 전송이 끝났나기만 했다면 아무 에러가 없다.
        body.map { System.out.println(it) }.block()
    }
}
