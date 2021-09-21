package com.npcode.learning.kotlin

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import kotlin.jvm.internal.FunctionReference

class FailSafeTest {

    @Test
    fun test() {
        shouldThrow<RuntimeException> {
            f1()
        }

        shouldNotThrow<RuntimeException> {
            failSafeProxy(::f1)()
        }
    }

    @Test
    fun test3() {
        val failSafeF3 = failSafeProxy(::f3)

        shouldThrow<RuntimeException> {
            f3(12)
        }

        shouldNotThrow<RuntimeException> {
            failSafeF3(34)
        }
    }
}

fun f3(x: Int): Unit {
    throw RuntimeException()
}

fun f1() {
    throw RuntimeException()
}

val logger = KotlinLogging.logger {}

fun <T> failSafeProxy(func: () -> T): () -> T? = {
    try {
        func.invoke()
    } catch (e: Throwable) {
        logger.warn("Failed to invoke ${(func as FunctionReference).owner}")
        null
    }
}

fun <A, R> failSafeProxy(func: (A) -> R): (A) -> R? = { a ->
    try {
        func.invoke(a)
    } catch (e: Throwable) {
        logger.warn("Failed to invoke ${(func as FunctionReference).owner}")
        null
    }
}

fun interface MyFunc {
    operator fun invoke()

    class Default : MyFunc {
        override fun invoke() {
            throw RuntimeException()
        }
    }
}
