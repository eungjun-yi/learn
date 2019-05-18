package org.springframework.web.reactive.function.server

import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

class MonoFieldTest {

    @Test
    fun testMonoField() {
        val foo = Foo(a = "abc", b = Mono.just("bbc"))

        val client = WebTestClient.bindToRouterFunction {
            Mono.just(
                HandlerFunction<ServerResponse> {
                    ServerResponse.ok().body(Mono.just(foo), Foo::class.java)
                }
            )
        }.build()

        // 실패한다. {"a":"abc","b":{"scanAvailable":true}}
        client.get().exchange().expectBody().json("""{"a":"abc","b":"bbc"}""")
    }

    data class Foo(
        val a: String,
        val b: Mono<String>
    )
}
