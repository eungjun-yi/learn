package com.npcode.learning.kotlin

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MyTypeKtTest {

    @Test
    fun typeName() {
        typeName(MyType.A) shouldBe "a"
        typeName(MyType.B) shouldBe "b"
    }
}
