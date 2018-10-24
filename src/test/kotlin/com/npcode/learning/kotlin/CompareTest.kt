package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CompareTest {

    data class Foo(
        val func: () -> Unit = { }
    )

    data class Bar(
        val value: Int = 1
    )

    @Test
    fun test() {
        val commonValue = 1
        val commonFunc = {}
        // success
        assertThat(Bar(commonValue)).isEqualTo(
            Bar(
                commonValue
            )
        )
        // fail
        // assertThat(com.npcode.learning.kotlin.Foo(commonFunc)).isEqualTo(com.npcode.learning.kotlin.Foo(commonFunc))
    }
}
