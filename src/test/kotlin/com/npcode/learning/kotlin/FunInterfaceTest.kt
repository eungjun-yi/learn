package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test

class FunInterfaceTest {
    @Test
    fun test() {
        val foo = MyFoo { 1 }
        println(foo())
    }
}

fun interface MyFoo {
    operator fun invoke(): Int
}
