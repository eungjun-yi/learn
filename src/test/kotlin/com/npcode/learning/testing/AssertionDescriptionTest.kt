package com.npcode.learning.testing

import im.toss.test.equalsTo
import io.kotest.core.spec.style.FreeSpec
import org.junit.jupiter.api.Test

class Junit5AssertionDescriptionTest {

    @Test
    fun `intellij에서 Click to see difference가 뜨는가`() {
        Foo("a").equalsTo(Foo("b")) // 뜬다.
        "a".equalsTo("b") // 뜬다
    }
}

class KotestAssertionDescriptionTest: FreeSpec({
    "intellij에서 Click to see difference가 뜨는가" - {
        Foo("a").equalsTo(Foo("b")) // 뜬다.
        "a".equalsTo("b") // 뜬다
    }
})

data class Foo(val value: String)
