package com.npcode.learning.kotlin

import FailSafe3
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.jvmName

class FailSafeProxyByInterfaceTest {

    @Test
    fun test() {
        val getNumber = GetNumber { throw RuntimeException() }

        shouldThrow<RuntimeException> {
            getNumber()
        }

        shouldNotThrow<RuntimeException> {
            getNumber.failSafe()
        }
    }

    @Test
    fun test2() {
        val getNumber = GetNumber2 { throw RuntimeException() }

        shouldThrow<RuntimeException> {
            getNumber()
        }

        // java.lang.AbstractMethodError: Receiver class com.npcode.learning.kotlin.FailSafeProxyByGenericsTest$test2$getNumber$1 does not define or inherit an implementation of the resolved method 'abstract java.lang.Object failSafe()' of interface com.npcode.learning.kotlin.FailSafe2.
        shouldNotThrow<RuntimeException> {
            getNumber.failSafe()
        }
    }

    @Test
    fun test3() {
        val getNumber = GetNumber3 { throw RuntimeException() }

        shouldThrow<RuntimeException> {
            getNumber()
        }

        shouldNotThrow<RuntimeException> {
            getNumber.failSafe()
        }
    }
}

fun interface FailSafe<R>  {
    operator fun invoke(): R

    fun failSafe() {
        try {
            invoke()
        } catch (e: Throwable) {
            val shortName = this::class.jvmName.split("$").reversed()[1]
            logger.warn("Failed to invoke $shortName (${this})")
        }
    }
}

// 이것을 구현하면 runtime error가 발생한다.
// java.lang.AbstractMethodError: Receiver class com.npcode.learning.kotlin.FailSafeProxyByGenericsTest$test2$getNumber$1 does not define or inherit an implementation of the resolved method 'abstract java.lang.Object failSafe()' of interface com.npcode.learning.kotlin.FailSafe2.
// java라면 컴파일에러가 난다
fun interface FailSafe2<R>  {
    operator fun invoke(): R

    fun failSafe(): R? {
        return try {
            invoke()
        } catch (e: Throwable) {
            logger.warn("Failed to invoke ${this::class.simpleName}}")
            null
        }
    }
}

fun interface GetNumber : FailSafe<Int> {
    override operator fun invoke(): Int
}

fun interface GetNumber2 : FailSafe2<Int> {
    override operator fun invoke(): Int
}

fun interface GetNumber3 : FailSafe3<Int> {
    override operator fun invoke(): Int
}
