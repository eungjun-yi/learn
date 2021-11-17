package com.npcode.learning.coroutine

import io.kotest.fp.success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Test

class TimeoutTest {

    @Test
    fun test() {
        runBlocking {
            try {
                withTimeout(10) {
                    println("a")
                    delay(20)
                    println("b")
                }
            } catch (e: Exception) {
                // TimeoutCancellationException 이 발생하면 캔슬되어서 println("b")는 실행이 안된다
                println("accepted")
            }
        }

        println("done")

        Thread.sleep(20)
    }

    @Test
    fun test2() {
        val job = CoroutineScope(Dispatchers.IO).async {
            println("a")
            println("delay 100ms")
            delay(200)
            println("b")
            throw RuntimeException("stop this!")
            println("c")
        }

        println(job.success())

        runBlocking {
            try {
                println("await with timeout 150ms")
                withTimeout(120) { job.await() } // launch로 하면 예외 전파가 안됨
                println("ok")
            } catch (e: TimeoutCancellationException) {
                // TimeoutCancellationException 이 발생하더라도 job은 중단되지 않고 실행된다. 아마 await()를 이미 호출했기 때문일 것이다.
                // 다만 이후에 job 안에서 발생한 예외는 전파될 수 없다
                println("accepted")
            }
        }

        println(job.success())

        println("sleep 150ms")
        Thread.sleep(150)

        println(job.success())
    }

    @Test
    fun test3() {
        runBlocking {
            try {
                withTimeout(50) {
                    withContext(Dispatchers.Default) {
                        println("a")
                        delay(100)
                        println("b")
                    }
                }
                println("ok")
            } catch (e: TimeoutCancellationException) {
                println("accepted")
            }
        }
    }
}
