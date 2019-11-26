package com.npcode.learning.kotlin

import im.toss.test.equalsTo
import org.junit.jupiter.api.Test

class DelegationTest {

    interface A: B {
        fun a(): Int
    }

    interface B {
        fun b(): Int
    }

    class FooA: A {
        override fun b() = 1
        override fun a() = 1
    }

    class FooB: B {
        override fun b() = 2
    }

    @Test
    fun `위임을 하면 어느 한 쪽이 덮어쓴다`() {
        val x: A = object: A by FooA(), B by FooB() {
            override fun b() = 3
        }
    }
}
