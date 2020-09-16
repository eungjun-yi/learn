package io.kotest

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ints.shouldBeEven
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeOdd

class WordSpecExample : WordSpec({
    "List" should {
        listOf(1, 2, 3).forEach {
            "$it is smaller than 4" {
                it.shouldBeLessThan(4)
            }
        }
    }
})

class StringSpecExample : FreeSpec({
    "자연수 중" - {
        listOf(1, 3, 5, 6).let {
            "$it 은 홀수여야한다" - {
                it.forEach { it.shouldBeOdd() }
            }
        }
        listOf(2, 4, 6).let {
            "$it 은 짝수여야한다" - {
                it.forEach { it.shouldBeEven() }
            }
        }
    }
})
