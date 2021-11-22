package com.npcode.learning.kotlin.coverage

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class WhenSealedCoverageTest {

    @Test
    fun test() {
        returnType(Foo()) shouldBe "Foo"
        returnType(Bar()) shouldBe "Bar"

        printType(Foo())
        printType(Bar())
    }
}
