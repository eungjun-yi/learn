package com.npcode.learning.kotlin

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CoroutineTest {

    @Test
    fun testBridging() {
        bridging()
    }

    fun bridging() = runBlocking {
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

    fun helloWorld() = runBlocking {
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
}
