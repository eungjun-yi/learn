package com.npcode.learning

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class SealedParentTest {

    @Test
    fun test() {
        returnType(Foo()) shouldBe "Foo"
        returnType(Bar()) shouldBe "Bar"

        printType(Foo())
        printType(Bar())
    }
}
