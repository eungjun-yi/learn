package arrow.syntax

import arrow.syntax.function.curried
import im.toss.test.equalsTo
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class CurryingTest {

    @Test
    fun test() {
        foo.equalsTo(foo)
        foo.curried().equalsTo(foo.curried())
        foo.curried()("hello").equalsTo(foo.curried()("hello"))
    }

    @Test
    fun test2() {
        bar.equalsTo(bar)
        bar("hello").equalsTo(bar("hello"))
    }

    @Test
    fun test3() {
        Baz("hello").equalsTo(Baz("hello"))
        Baz("hello")("world").equalsTo(Baz("hello")("world"))
    }

    @Test
    fun test4() {
        // curried()와 성능 비교를 해 볼것
        foo.curried2()("hello").equalsTo(foo.curried2()("hello"))
        foo.curried2()("hello")("world").equalsTo("hello world")
    }

    @Test
    fun perf() {
        // 성능 차이가 크지 않은 것 같다

        val b = measureTimeMillis {
            repeat(10000000) { foo.curried2()("hello")("hello") }
        }

        val a = measureTimeMillis {
            repeat(10000000) { foo.curried()("hello")("hello") }
        }

        val d = measureTimeMillis {
            repeat(10000000) { foo.curried2()("hello")("hello") }
        }

        val c = measureTimeMillis {
            repeat(10000000) { foo.curried()("hello")("hello") }
        }

        println("${a+c} ${b+d}")
    }
}

data class Curried1<P1, P2, R>(
    private val p1: P1,
    private val next: (P1, P2) -> R
) {
    operator fun invoke(p2: P2) = next(p1, p2)
}

private fun <P1, P2, R> ((P1, P2) -> R).curried2() = { p1: P1 -> Curried1(p1, this) }

private val foo = { x: String, y: String -> "$x $y" }
private val bar = { x: String -> { y: String -> "$x $y" } }

data class Baz(
    val x: String
) {
    operator fun invoke(y: String) = "$x $y"
}

