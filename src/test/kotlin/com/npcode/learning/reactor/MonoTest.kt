package com.npcode.learning.reactor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

class MonoTest {

    @Test(expected = java.lang.RuntimeException::class)
    fun test() {
        val mono: Mono<String> = Mono.just("y").flatMap {
            if (it == "x") Mono.just("X") else Mono.error(RuntimeException())
        }
        mono.block()
    }

    @Test
    fun repeat() {
        val mono = Mono.fromCallable { System.out.println("publisher works"); 1 }
        // map 할 때 마다 publisher가 실행된다.
        mono.map { it * 2 }.block()
        mono.map { it * 2 }.block()
    }

    @Test
    fun cache() {
        val mono = Mono.fromCallable { System.out.println("publisher works"); 1 }.cache()
        // cache 하면 한번만 실행된다.
        mono.map { it * 2 }.block()
        mono.map { it * 2 }.block()
    }

    @Test
    fun async() {
        // Given
        val scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(2))
        val mono = Mono.fromCallable { System.out.println("publisher works"); Thread.sleep(100); 1 }.publishOn(scheduler)

        // When
        val start = System.currentTimeMillis()
        val mono1 = mono.map { it * 2 }
        val mono2 = mono.map { it * 2 }
        val mono3 = mono.map { it * 2 }
        Mono.zip(mono1, mono2, mono3).block()
        val spentMillis = System.currentTimeMillis() - start

        // Then
        assertThat(spentMillis).isGreaterThanOrEqualTo(200)
        assertThat(spentMillis).isLessThan(300)
    }
}
