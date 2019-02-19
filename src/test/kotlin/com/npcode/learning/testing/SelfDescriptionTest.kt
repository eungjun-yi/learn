package com.npcode.learning.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.temporal.Temporal

// Growing Object Oriented Software guided by tests 23.5
class SelfDescriptionTest {

    @Test
    fun `결제일은 end date와 같다 - assertj 설명이 장황하다`() {
        // Given
        // val startDate = LocalDate.of(2018, 1, 1)
        val startDate = namedDate(LocalDate.of(2018, 1, 1), "startDate")
        val endDate = namedDate(LocalDate.of(2018, 1, 15), "endDate")

        // When
        val paymentDate = paymentDate(startDate, endDate)

        // Then
        assertThat(paymentDate).`as`("결제일").isEqualTo(endDate)
    }

    @Test
    fun `결제일은 end date와 같다 - junit5 이게 조금 더 나은 것 같다`() {
        // Given
        // val startDate = LocalDate.of(2018, 1, 1)
        val startDate = namedDate(LocalDate.of(2018, 1, 1), "startDate")
        val endDate = namedDate(LocalDate.of(2018, 1, 15), "endDate")

        // When
        val paymentDate = paymentDate(startDate, endDate)

        // Then
        assertEquals(endDate, paymentDate, "결제일")
    }

    @Test
    fun `결제일은 end date와 같다 - kotlin test 이것도 어째선지 메시지가 두 번 나옴`() {
        // Given
        // val startDate = LocalDate.of(2018, 1, 1)
        val startDate = namedDate(LocalDate.of(2018, 1, 1), "startDate")
        val endDate = namedDate(LocalDate.of(2018, 1, 15), "endDate")

        // When
        val paymentDate = paymentDate(startDate, endDate)

        // Then
        kotlin.test.assertEquals(endDate, paymentDate, "결제일")
    }

    // 여기서 Temporal도 type parameter로 바꿀 방법이 있으면 좋겠다
    // mocking framework를 쓰면 간단히 될 것이지만...
    private fun <T: Temporal> namedDate(delegate: T, name: String): Temporal {
        return object : Temporal by delegate {
            override fun toString(): String {
                return name
            }
        }
    }
}

fun <T: Temporal> paymentDate(startDate: T, endDate: T): T {
    return startDate
}
