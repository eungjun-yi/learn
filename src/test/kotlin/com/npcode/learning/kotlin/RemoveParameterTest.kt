package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RemoveParameterTest {

    fun foo(
        a: Int = 1,
        c: Int = 3
    ): Int {
        return a + c
    }

    @Test
    fun test() {
        assertThat(foo(a = 1)).isEqualTo(5)
    }
}
