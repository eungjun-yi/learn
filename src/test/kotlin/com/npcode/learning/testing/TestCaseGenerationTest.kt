package com.npcode.learning.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction

class TestCaseGenerationTest {

    @Test
    fun testGenerate() {
        val pair = Given(a = true, b = true) to true
        val generator = TestCaseGenerator<Given>()
        val func = { it: Given, value: Boolean -> it.copy(a = value) }
        val a = BooleanPropertyUpdater(
            update = { it: Given, value: Boolean -> it.copy(a = value) }
        )

        val b = BooleanPropertyUpdater(
            update = { it: Given, value: Boolean -> it.copy(b = value) }
        )
        val set = generator.generateReversedPairs(
            pair,
            a,
            b
        )

        assertThat(set).containsAll(
            listOf(
                Given(a = true, b = false) to false,
                Given(a = false, b = true) to false,
                Given(a = false, b = false) to false
            )
        )
    }

    @Test
    fun testGenerateAllPossibleDataWhichMatchesGivenCondition() {
        data class Given(
            val a: Boolean = false,
            val b: Boolean = false,
            val c: Boolean = false
        )

        val base = Given()
        val propertyA = BooleanPropertyUpdater<Given> { it, x -> it.copy(a = x) }
        val propertyB = BooleanPropertyUpdater<Given> { it, x -> it.copy(b = x) }

        val prop = Given::a
        val copier = Given::copy

        val trueAllOf = trueAllOf2(base, copier)
        assertThat(trueAllOf).isEqualTo(Given(a = true, b = true, c = true))
        assertThat(trueAllOf(base, listOf(propertyA, propertyB))).isEqualTo(Given().copy(a = true, b = true))
        assertThat(trueAnyOf(base, listOf(propertyA, propertyB))).containsAll(
            listOf(
                Given().copy(a = true, b = false),
                Given().copy(a = false, b = true),
                Given().copy(a = true, b = true)
            )
        )
        assertThat(falseAnyOf(base, listOf(propertyA, propertyB))).containsAll(
            listOf(
                Given().copy(a = true, b = false),
                Given().copy(a = false, b = true),
                Given().copy(a = false, b = false)
            )
        )
    }

    private fun <T> trueAllOf2(base: T, copier: KFunction<T>): T {
        val params = generateSequence { true }.take(copier.parameters.size - 1).toList().toTypedArray()
        return copier.call(base, *params)
    }
}

data class Given(
    val a: Boolean,
    val b: Boolean
)

