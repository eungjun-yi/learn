package com.npcode.learning.reactor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
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

    @Test
    fun thread() {
        val mono = Mono.just("hello").map { "$it thread " }

        val t = Thread({
            mono.subscribe {
                System.out.println(it + Thread.currentThread().name)
            }
        }, "mythread")

        t.start()

        t.join()
    }

    @Test
    fun runnable() {
        val t = Thread { System.out.println("hello " + Thread.currentThread().name) }
        Mono.fromRunnable<String>(t)
            .block()
    }

    @Test
    fun doOnErrorNeedsSubscribe() {
        Mono.fromCallable {
            throw RuntimeException()
            ""
        }
            .doOnError { Mono.fromCallable { System.out.println("do something on error") }.subscribe() }
            .onErrorReturn("")
            .block()
    }

    @Test
    fun doOnEachRunsOnlyOnce() {
        open class Foo {
            fun bar() {

            }
        }
        val foo = mock<Foo> {}
        Mono.just(1)
            .doOnEach { foo.bar() }
            .block()

        verify(foo, times(1)).bar()
    }

    @Test
    fun doOnEachNeverRunIfEmpty() {
        open class Foo {
            fun bar() {

            }
        }
        val foo = mock<Foo> {}
        Mono.empty<Int>()
            .doOnEach { foo.bar() }
            .block()

        verify(foo, never()).bar()
    }

    @Test
    fun doOnEachNeverRunIfError() {
        open class Foo {
            fun bar() {

            }
        }
        val foo = mock<Foo> {}
        Mono.fromCallable { throw IllegalStateException() }
            .doOnEach { foo.bar() }
            .onErrorResume { Mono.empty() }
            .block()

        verify(foo, never()).bar()
    }

    @Test
    fun doOnSuccessNeverRunIfEmpty() {
        open class Foo {
            fun bar() {

            }
        }
        val foo = mock<Foo> {}
        Mono.just(1)
            .flatMap { Mono.empty<Int>() }
            .doOnSuccess { foo.bar() }
            .block()

        verify(foo, never()).bar()
    }

    @Test
    fun nullToEmptyMono() {
        assertThrows<NullPointerException> {
            Mono.just(null)
        }

        assertThrows<NullPointerException> {
            Mono.just(1).map { null }.block()
        }

        val x: Int? = null

        assertThrows<NullPointerException> {
            Mono.just(1).map { x }.filter { it != null }.block()
        }

        Mono.just(1).flatMap { x?.toMono() ?: Mono.empty() }
    }

    @Test
    fun zipWitEmpty() {
        // then을 쓰면 될까? 안되잖아
        val value: Mono<Int> = Mono.just(1)
        val emptyThen: Mono<Void> = Mono.empty<Int>().then()

        Mono.zip(value, emptyThen)
            .map {
                System.out.println(it.t1)
            }.block()
    }

    @Test
    fun concatWithEmpty() {
        val value: Mono<Int> = Mono.just(1)
        val empty: Mono<Int> = Mono.empty<Int>()

        Flux.concat(value, empty).map {
            System.out.println(it)
        }.blockLast()
    }
}
