package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ReifiedTest {

    class Foo {
        fun bar1() = "bar1"

        companion object {
            fun bar2() = "bar2"
        }
    }

    inline fun <T : Foo> foo1(t: T) {
        Foo.bar2()
        // T::class.java // compile error
        t.bar1()
        // T.bar2() // compile error
    }

    inline fun <reified T : Foo> foo2(t: T) {
        Foo.bar2()
        T::class.java
        t.bar1()
        // T.bar2() // compile error
    }

    @Test
    fun reflectionAndInline() {

        class A {
            val name = genName().javaClass.name
        }

        class B {
            val name = genName().javaClass.name
        }

        assertThat(A().name).isEqualTo("com.npcode.learning.kotlin.ReifiedTest\$reflectionAndInline\$A\$name\$1")
        assertThat(B().name).isEqualTo("com.npcode.learning.kotlin.ReifiedTest\$reflectionAndInline\$B\$name\$1")
    }

    inline fun genName()  = {}
}
