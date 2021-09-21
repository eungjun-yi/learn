package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test

class GenericsTest {

    @Test
    fun test() {
    }

    @Test
    fun outTest() {
        // compile error
        // val ls8: D<A> = D<B>()
        val ls8: D<out A> = D<B>()
    }

    open class A {}
    class B : A() {}
    class C : A() {}

    class D<T> {
        fun foo1(arg: T) {}
        fun foo2(arg: T): T = arg
    }
}

class FooFoo<T>  {
    fun foo(x: T) = ""
}
