package io.mockk

import im.toss.test.equalsTo
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class MockkTest {

    @Test
    fun test() {
        data class Foo(
            val bar: (Long) -> String
        )
        val foo = mockk<Foo>(relaxed = true)
        foo.bar(1).equalsTo("")
    }

    @Test
    fun `stubbing되지 않은 메서드를 호출하면 MockkException이 발생한다`() {
        class Bar {
            fun do1() = 1
            fun do2() = 2
        }

        val bar: Bar = mockk()
        every { bar.do1() } returns 3

        bar.do1().equalsTo(3)
        shouldThrow<MockKException> {
            bar.do2().equalsTo(2)
        }
    }

    @Test
    fun `relaxed=true인경우 stubbing되지 않은 메서드를 호출하면 가짜 구현으로 동작한다`() {
        class Bar {
            fun do1() = 1
            fun do2() = 2
        }

        val bar: Bar = mockk(relaxed = true)
        every { bar.do1() } returns 3

        bar.do1().equalsTo(3)
        bar.do2().equalsTo(0)
    }

    @Test
    fun `구체 클래스의 mock을 만들면, 그 타입은 mock이 아니더라도 proxy를 거치게 된다`() {
        class Bar {
            fun do1() = 1
        }

        val bar1: Bar = mockk()
        every { bar1.do1() } returns 3

        bar1.do1().equalsTo(3)
        Bar().do1().equalsTo(1) // 이 호출도 mockk proxy를 거치게 된다. 왜냐하면 Bar 자체가 교체되었기 때문이다.
    }
}
