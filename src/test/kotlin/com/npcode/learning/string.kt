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

    @Test
    fun hashCollision() {
        System.out.println(findWord("", "INVALID_USER".hashCode()))
    }

    private fun findWord(base: String, hashCode: Int): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + ('_')

        var words = listOf("")

        while(true) {
            val nextWords = words.flatMap { w -> charPool.map { w + it } }

            val match = nextWords.firstOrNull {
                System.out.println(it)
                it.hashCode() == hashCode
            }

            if (match != null) {
                return match
            }

            words = nextWords
        }
    }
}

