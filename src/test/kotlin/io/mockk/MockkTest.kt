package io.mockk

import im.toss.test.equalsTo
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
}
