package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

abstract class FooTest {
    private val foo = makeFoo()

    private fun makeFoo(): Int {
        System.out.println("We make foo")
        return 1
    }

    @Test
    fun test1() {
        assertThat(foo).isEqualTo(1)
    }

    @Test
    fun test2() {
        assertThat(foo).isEqualTo(1)
    }
}


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerClassTest: FooTest()

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PerMethodTest: FooTest()

class DefaultTest: FooTest()
