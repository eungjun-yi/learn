package com.npcode.learning.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.MethodSource

class MethodSourceTest {

    @Test
    @MethodSource("mydata")
    fun test(data: List<String>) {
        assertThat(data).isEqualTo(listOf("a", "b"))
    }

    companion object {
        fun mydata() = listOf("a", "b")
    }
}
