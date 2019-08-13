package com.npcode.learning.testing

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object CalculatorSpec: Spek({
    given("a calculator") {
        val calculator = SampleCalculator()
        on("addition") {
            val sum = calculator.sum(2, 4)
            it("should return the result of adding the first number to the second number") {
                assertThat(sum).isEqualTo(6)
            }
        }
        on("subtraction") {
            val subtract = calculator.subtract(4, 2)
            it("should return the result of subtracting the second number from the first number") {
                assertThat(subtract).isEqualTo(2)
            }
        }
    }
})

class SampleCalculator {
    fun sum(a: Int, b: Int) = a + b
    fun subtract(a: Int, b: Int) = a - b
}