package com.npcode.learning.kotlin

class WhenTest {

    fun test(foo: Foo): String {
        return when(foo) {
            Foo.A -> "a"
            Foo.B -> "b"
        }
    }
}

enum class Foo {
    A,
    B
}
