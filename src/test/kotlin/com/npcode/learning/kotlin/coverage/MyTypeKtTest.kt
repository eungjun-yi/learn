package com.npcode.learning.kotlin.coverage

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class MyTypeKtTest {

    @Test
    fun typeName() {
        typeName(MyType.A) shouldBe "a"
        typeName(MyType.B) shouldBe "b"
        typeName(null) shouldBe "b"
    }
}
