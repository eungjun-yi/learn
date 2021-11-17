package com.npcode.learning.kotlin

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.jvmName

class FailSafeProxyByGenericTest {

    @Test
    fun test() {
        val getNumber = { throw RuntimeException() }
        val failSafeGetNumber: FailSafe5<Int, () -> Int> = FailSafe5(getNumber)

        shouldThrow<RuntimeException> {
            getNumber()
        }

        shouldNotThrow<RuntimeException> {
            failSafeGetNumber()
        }
    }

    @Test
    fun test2() {
        val getNumber = GetNumber5 { throw RuntimeException() }
        val failSafeGetNumber1: FailSafe5<Int, GetNumber5> = FailSafe5(getNumber) // Int를 생략할수만 있으면 딱 좋은데
        val failSafeGetNumber2: FailSafe6<Int> = FailSafe6(getNumber) // 오히려 GetNumber5를 생략할수는 있다. 근데 GetNumber5라는 걸 숨기고 싶진 않긴하다.
        // 역시 인터페이스를 이용하는게 제일 나아보인다.
    }
}

fun interface GetNumber5 : Function0<Int> {
    override operator fun invoke(): Int
}

class FailSafe5<R, F : () -> R>(
    private val op: F
) {
    operator fun invoke(): R? {
        return try {
            op.invoke()
        } catch (e: Throwable) {
            val shortName = op::class.jvmName.split("$").reversed()[1]
            logger.warn("Failed to invoke $shortName (${this})")
            null
        }
    }
}

class FailSafe6<R>(
    private val op: () -> R
) {
    operator fun invoke(): R? {
        return try {
            op.invoke()
        } catch (e: Throwable) {
            val shortName = op::class.jvmName.split("$").reversed()[1]
            logger.warn("Failed to invoke $shortName (${this})")
            null
        }
    }
}

fun test(a: Int): Int {
    return when {
        a > 10 -> 1
        a == 8 -> 2
        a == 4 -> 3
        else -> 4
    }
}
