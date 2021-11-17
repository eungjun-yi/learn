package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test


class ResultTest {

    @Test
    fun test() {
        println(foo().getOrNull())
    }

    fun foo(): Result<Int> {
        return Result.success(1)
    }
}
