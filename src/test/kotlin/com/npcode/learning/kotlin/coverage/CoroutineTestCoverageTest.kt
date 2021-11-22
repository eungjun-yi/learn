package com.npcode.learning.kotlin.coverage

import com.npcode.learning.coroutine.hello
import com.npcode.learning.coroutine.suspendHello
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
