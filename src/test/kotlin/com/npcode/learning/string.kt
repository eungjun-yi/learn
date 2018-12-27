package com.npcode.learning

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StringInterpolationTest {

    @Test
    fun testMap() {
        val map = mapOf("abc" to "def")
        assertThat("$map").isEqualTo("{abc=def}")
        assertThat(map.toString()).isEqualTo("{abc=def}")
    }
}
