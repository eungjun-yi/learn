package com.npcode.learning.kotlin

import im.toss.test.doesNotEqualTo
import im.toss.test.equalsTo
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.test
import java.time.Duration

class CoroutineTest {

    @Test
    fun testBridging() {
        bridging()
    }

    private fun bridging() = runBlocking {
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope { // Creates a new coroutine scope
            launch {
                delay(500L)
                println("Task from nested launch")
            }

            delay(100L)
            println("Task from coroutine scope") // This line will be printed before nested launch
        }

        println("Coroutine scope is over") // This line is not printed until nested launch completes
    }

    @Test
    fun testSuspendFunction() {
        helloWorld()
    }

    private fun helloWorld() = runBlocking {
        launch { doWorld() }
        println("Hello, ")
    }

    suspend fun doWorld() {
        delay(1000L)
        println("World!")
    }

    @Test
    fun testRepeat() = runBlocking {
        repeat(100) {
            launch {
                delay(1)
                println("Hello, $it")
            }

            launch {
                delay(2)
                println("World, $it")
            }
        }
    }

    @Test
    fun testRepeat2() = runBlocking {
        coroutineScope {
            launch {
                repeat(1000) {
                    delay(2)
                    println("World, $it")
                }
            }

            // 병렬로 반복하는게 아니라 순차적으로 실행한다
            repeat(1000) {
                delay(1)
                println("Hello, $it")
            }
        }

        // 아래 라인이 없으면 아무것도 출력되지 않는다. 왜??
        println("Coroutine scope is over") // This line is not printed until nested launch completes
    }

    @Test
    fun fluxToCoroutine() = runBlocking {
        val flux = Flux.just(1, 2, 3)
        println(flux.awaitFirst())
        println(flux.awaitLast())
    }

    @Test
    fun monoToCoroutine()  = runBlocking {
        val listMono1 = Flux.just(1, 2, 3).collectList()
        println(listMono1.awaitSingle())
        val listMono2 = Mono.empty<Int>()
        println(listMono2.awaitFirstOrNull())
    }

    @Test
    fun monoToCoroutineToMono() {
        val monoDatasource = Mono.just(1)
        val coroutineDatasource = CoroutineDatasource(monoDatasource)
        val multiplier = CoroutineService(coroutineDatasource)
        mono {
            multiplier.multiply(3)
        }.block()
    }

    class CoroutineService(private val datasource: CoroutineDatasource) {
        suspend fun multiply(n: Int): Int {
            return datasource.get() * n
        }
    }

    class CoroutineDatasource(private val monoDatasource: Mono<Int>) {
        suspend fun get() = monoDatasource.doOnEach {
            println("Context: " + it.context)
        }.awaitSingle()
    }

    @Test
    fun nullToEmptyMono() {
        mono {
            null
        }.test().verifyComplete()
    }

    @Test
    fun testContext() {
        val monoDatasource = Mono.just(1)
        val coroutineDatasource = CoroutineDatasource(monoDatasource)
        val multiplier = CoroutineService(coroutineDatasource)
        mono {
            multiplier.multiply(3)
        }.subscriberContext { it.put("myvalue", "foo") }.block()
    }

    @Test
    fun callSuspendFunctionTest() {
        runBlocking {
            foo(::bar1)
        }
    }

    @Test
    fun callSuspendFunctionTest2() {
        runBlocking {
            foo(::bar2)
        }
    }

    suspend fun foo(bar: suspend () -> Unit) = bar()

    fun bar1() = 1

    suspend fun bar2(): Int {
        delay(1)
        return 1
    }
}
