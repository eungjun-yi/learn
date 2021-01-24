package com.npcode.learning

import feign.Feign
import feign.FeignException
import feign.RequestLine
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class FeignTest {

    @Test
    fun test() {
        val client = Feign.builder().target(FooFeignClient::class.java, "https://example.org")
        shouldThrow<FeignException.NotFound> {
            client.get()
        }
    }

}

interface FooFeignClient {
    @RequestLine("GET /bad")
    fun get(): String
}
