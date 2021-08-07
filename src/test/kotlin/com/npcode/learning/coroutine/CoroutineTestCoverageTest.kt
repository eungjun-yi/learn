package com.npcode.learning.coroutine

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CoroutineTestCoverageTest {

    @Test
    fun test() {
        hello()
    }

    @Test
    fun test2() {
        runBlocking {
            suspendHello()
        }
    }
}
