package com.npcode.learning.kotlin

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Period

internal class BigDecimalTest {

    @Test
    fun testBigDecimal() {
        val interestRate = 0.025.toBigDecimal()
        val period = Period.ofMonths(6)

        val interestMultiDays = (250 * period.days).toBigDecimal()

        interestMultiDays shouldBe 45000

        (interestMultiDays * interestRate / 365.toBigDecimal()).toLong() shouldBe 125
    }
}
