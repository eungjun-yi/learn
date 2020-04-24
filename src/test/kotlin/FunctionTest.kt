import arrow.syntax.function.andThen
import im.toss.test.doesNotEqualTo
import im.toss.test.equalsTo
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

typealias IntFunction = () -> Int
typealias StringFunction = () -> String

class FunctionTest {

    @Test
    fun test1() {
        val foo1: IntFunction = { 1 }
        val foo2: IntFunction = { 1 }

        foo1::javaClass.doesNotEqualTo(foo2::javaClass)
        foo1.javaClass.doesNotEqualTo(foo2.javaClass)
        foo1::class.doesNotEqualTo(foo2::class)
        foo1::class.java.doesNotEqualTo(foo2::class.java)
    }

    @Test
    fun testIsAssignable() {
        val foo1: IntFunction = { 1 }
        val foo2: StringFunction = { "a" }
        val foo3: () -> Int = { 1 }
        val foo4: () -> String = { "a" }

        val foo5: IntFunction = { 1 }
        val foo6: StringFunction = { "a" }
        val foo7: () -> Int = { 1 }
        val foo8: () -> String = { "a" }

        foo1::class.java.isAssignableFrom(foo2::class.java).equalsTo(false)
        foo1::class.java.isAssignableFrom(foo3::class.java).equalsTo(false)
        foo3::class.java.isAssignableFrom(foo4::class.java).equalsTo(false)
        foo2::class.java.isAssignableFrom(foo4::class.java).equalsTo(false)

        foo1::class.java.isAssignableFrom(foo5::class.java).equalsTo(true)
    }

    @Test
    fun test2() {
        val foo1: () -> Int = { 1 }
        val foo2: () -> Int = { 1 }

        foo1::javaClass.doesNotEqualTo(foo2::javaClass)
        foo1.javaClass.doesNotEqualTo(foo2.javaClass)
        foo1::class.doesNotEqualTo(foo2::class)
        foo1::class.java.doesNotEqualTo(foo2::class.java)
    }

    @Test
    fun ref() {
        val x = ref<() -> Int>()
        x().equalsTo(1)
    }

    @Test
    fun typealiasTest() {
    }
}

typealias One = () -> Int

inline fun <reified T> ref(): T {
    return when(T::class.java) {
        kotlin.jvm.functions.Function0::class.java -> { { 1 } as T }
        else -> throw RuntimeException()
    }
}

