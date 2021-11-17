package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test

class ContractTest {
    @Test
    fun test() {
        foo(0)
    }

    fun foo(x: Int?) {
        require(x != null)
        println(x + 1)
    }
}
