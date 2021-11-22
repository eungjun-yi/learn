package com.npcode.learning.kotlin.coverage


fun doSomethingWithWhen(a: String): Int {
    return when (a) {
        "a" -> 0
        "b" -> 1
        else -> 2
    }
}
