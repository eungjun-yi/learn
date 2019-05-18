package org.springframework.web.reactive.function.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import java.net.URI

class RouterFunctionTest {

    private val foo1Type = MediaType.valueOf("application/vnd.toss.foo1+json")
    private val foo2Type = MediaType.valueOf("application/vnd.toss.foo2+json")

    @Test
    fun testContentNegotiation() {
        // quality value는 무시하고 그냥 서버가 정의한 우선순위대로 결정된다.
        testContentNegotiation(foo1Type.toString(), foo1Type)
        testContentNegotiation(foo2Type.toString(), foo2Type)
        testContentNegotiation("$foo1Type, $foo2Type;q=0.9", foo1Type)
        testContentNegotiation("$foo2Type;q=0.9, $foo1Type", foo1Type)
        // testContentNegotiation("$foo1Type;q=0.9, $foo2Type", foo2Type) // 실패
        // testContentNegotiation("$foo2Type, $foo1Type;q=0.9", foo2Type) // 실패
    }

    @Test
    fun testNotAcceptable() {
        val request = MockServerRequest
            .builder()
            .header(HttpHeaders.ACCEPT, foo2Type.toString())
            .uri(URI.create("/"))
            .build()

        val r = router {
            accept(foo1Type).nest {
                GET("/") {
                    ServerResponse.ok().build()
                }
            }
        }

        val response = r.route(request).flatMap {
            it.handle(request)
        }.block()!!

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    private fun testContentNegotiation(
        requestAcceptHeader: String,
        responseContentType: MediaType
    ) {
        val request = MockServerRequest
            .builder()
            .header(HttpHeaders.ACCEPT, requestAcceptHeader)
            .uri(URI.create("/"))
            .build()

        val r = router {
            accept(foo1Type).nest {
                GET("/") {
                    ServerResponse.ok().contentType(foo1Type).syncBody("\"$foo1Type\"")
                }
            }
            accept(foo2Type).nest {
                GET("/") {
                    ServerResponse.ok().contentType(foo2Type).syncBody("\"$foo2Type\"")
                }
            }
        }

        val response = r.route(request).flatMap {
            it.handle(request)
        }.block()!!

        assertThat(response.headers()[HttpHeaders.CONTENT_TYPE]).contains(responseContentType.toString())
    }
}
