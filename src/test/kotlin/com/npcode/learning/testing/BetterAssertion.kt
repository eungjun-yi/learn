package com.npcode.learning.testing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Growing Object Oriented Software guided by tests 24
class BetterAssertionTest {

    @Test
    fun testPlusOne() {
        val given = 5

        val addedOne = plusOne(given)

        // "given 보다 1 커야 한다"라고 assertion 할 방법은 없다.
        // assertThat(actual).isGreaterThan(given)

        // 이렇게 할 수는 있다.
        // assertThat(actual - given).`as`("actual과 given의 차").isEqualTo(0)
        // assertEquals(1, addedOne - given, "plusOne이 늘린 값의 크기")

        // 이런건 어떤가?
        val increasing = addedOne - given
        assertEquals(1, increasing, "plusOne이 늘린 값의 크기")
    }

    private fun plusOne(given: Int) = given + 0
}
