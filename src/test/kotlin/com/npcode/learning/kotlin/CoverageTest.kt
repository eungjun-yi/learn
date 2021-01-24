package com.npcode.learning.kotlin

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CoverageTest {

    @Test
    fun test() {
        getName(Person("김토스")) shouldBe "김토스"
        getName(null) shouldBe "아무개"
    }
}
