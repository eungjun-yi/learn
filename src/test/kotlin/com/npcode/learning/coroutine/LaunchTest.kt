package com.npcode.learning.coroutine

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class LaunchTest {

    var do1: Boolean = false
    var do2: Boolean = false

    @Test
    fun test1() {
        doTwoThings()
        do1 shouldBe true
        do2 shouldBe false
    }

    @Test
    fun test2() {
        runBlocking {
            doTwoThings().join()
        }
        do1 shouldBe true
        do2 shouldBe true
    }

    @Test
    fun test3() {
        runBlocking {
            doTwoThingsSuspend()
        }
        do1 shouldBe true
        do2 shouldBe true
    }

    @Test
    fun joinEmptyJob() {
        runBlocking {
            Job().join()
        }
    }

    fun doTwoThings(): Job {
        do1 = true
        return GlobalScope.launch {
            delay(100)
            do2 = true
        }
    }

    suspend fun doTwoThingsSuspend() = coroutineScope {
        do1 = true
        launch {
            delay(100)
            do2 = true
        }
    }
}
