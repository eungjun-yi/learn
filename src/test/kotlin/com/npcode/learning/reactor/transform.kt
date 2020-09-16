package com.npcode.learning.reactor

import im.toss.test.equalsTo
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class PublisherTransformTest {

    @Test
    fun listOfMonoToFlux() {
        val given = listOf(
            Mono.just(1)
        )

        Flux.fromIterable(given).collectList().block().equalsTo(listOf(1))
    }

    @Test
    fun fluxOfMonoToFlux() {
        val given = Flux.just(
            Mono.just(1)
        )

        val xs = given.flatMap { it }
    }
}
