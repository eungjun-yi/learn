package com.npcode.learning.kotlin.coverage

enum class MyType {
    A, B
}

// kotlin 1.5, jacoco 0.8.7에서 1 of 5 branch missed
// type이 nullable이면 이런 문제가 있다.
fun typeName(type: MyType?) = when (type) {
    MyType.A -> "a"
    MyType.B, null -> "b"
}
