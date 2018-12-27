package com.npcode.learning.kotlin

class MonadTest {

    fun test() {
        // map은 결과를 add, flatMap은 addAll
        listOf(1, 2, 3).map { listOf(it) }
        listOf(1, 2, 3).flatMap { listOf(it) }
    }
}

