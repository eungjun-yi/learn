package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ExceptionTest {

    @Test
    fun test() {
        try {
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            println("good")
        } catch (e: RuntimeException) {
            fail { "여기서 잡히면 안됨" }
        }
    }
}
