package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ProtectedMemberTest {

    @Test
    fun test() {
        val bar = object: ProtectedMethodHolder() {
            override fun foo() = 2
        }.bar()

        assertThat(bar).isEqualTo(2)
    }
}
