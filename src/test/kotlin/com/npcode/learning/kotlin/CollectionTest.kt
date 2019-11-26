package com.npcode.learning.kotlin

import im.toss.test.equalsTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CollectionTest {

    data class Field(val a: Int, val b: Int)

    @Test
    fun sortedByBoolean() {
        // false가 true보다 우선순위가 높다
        assertThat(listOf(1, 2, 3).sortedBy { it == 3 }).isEqualTo(listOf(1, 2, 3))
        assertThat(listOf(1, 2, 3).sortedBy { it != 3 }).isEqualTo(listOf(3, 1, 2))
    }

    @Test
    fun sortedByOrdering() {
        val list = listOf(Field(2, 2), Field(1, 2), Field(2, 1))
        val sortedByA = listOf(Field(1, 2), Field(2, 2), Field(2, 1))
        val sortedByB = listOf(Field(2, 1), Field(2, 2), Field(1, 2))
        val sortedByAAndB = listOf(Field(1, 2), Field(2, 1), Field(2, 2))
        val sortedByBAndA = listOf(Field(2, 1), Field(1, 2), Field(2, 2))

        assertThat(list.sortedBy { it.a }).isEqualTo(sortedByA)
        assertThat(list.sortedBy { it.b }).isEqualTo(sortedByB)
        assertThat(list.sortedBy { it.b }.sortedBy { it.a }).isEqualTo(sortedByAAndB)
        assertThat(list.sortedBy { it.a }.sortedBy { it.b }).isEqualTo(sortedByBAndA) // 나중에 나온 것이 우선순위가 높다
    }

    @Test
    fun chainByFold() {
        listOf(1, 2, 3).fold(10) { acc, i ->
            acc + i
        }.equalsTo(16)
    }

    @Test
    fun toMap() {
        listOf(
            "a" to "b",
            "a" to "c"
        ).groupBy(
            { it.first },
            { it.second }
        )
        .toMap().equalsTo(
            mapOf(
                "a" to listOf("b", "c")
            )
        )
    }
}
