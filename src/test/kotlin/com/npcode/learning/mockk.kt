package com.npcode.learning

import io.mockk.spyk
import io.mockk.verify
import org.junit.Test

class MockkTest {

    @Test
    fun test() {
        class Foo {
            fun hello()  = hello("world")
            fun hello(name: String) = "hello, $name"
        }
        val foo = spyk(Foo())
        foo.hello()
        verify {
            foo.hello("world")
            // foo.hello("me") // fail
        }
    }
}