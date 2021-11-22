package com.npcode.learning.kotlin.coverage

sealed class SealedParent

class Foo: SealedParent()

class Bar: SealedParent()

// can be 100% covered
fun returnType(sealed: SealedParent): String {
    return when(sealed) {
        is Foo -> "Foo"
        is Bar -> "Bar"
    }
}

// cannot be 100% covered
fun printType(sealed: SealedParent) {
    when(sealed) {
        is Foo -> println("Foo")
        is Bar -> println("Bar")
    }
}
