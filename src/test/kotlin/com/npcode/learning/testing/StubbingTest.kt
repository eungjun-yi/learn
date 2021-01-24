package com.npcode.learning.testing

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class StubbingTest {

    @Test
    fun test() {
        val foo1 = mockk<Foo>()

        every {
            foo1.doSomething(1)
        } returns 100

        foo1.doSomething(1) shouldBe 100

        every {
            foo1.doSomething(2)
        } returns 200

        foo1.doSomething(2) shouldBe 200
        foo1.doSomething(1) shouldBe 100
    }

    class Foo {
        fun doSomething(input: Int): Int = input
    }
}
