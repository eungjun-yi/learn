package com.npcode.learning.kotlin

enum class MyType {
    A, B
}

fun typeName(type: MyType) = when (type) {
    MyType.A -> "a"
    MyType.B -> "b"
}
