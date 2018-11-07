package com.npcode.learning.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TimeTest {

    @Test
    fun betweenLocalDate() {
        val now = LocalDate.now()
        val actual = ChronoUnit.DAYS.between(now, now.plusDays(1))

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun betweenLocalDateTime() {
        val now = LocalDateTime.now()
        val actual = ChronoUnit.DAYS.between(now, now.plusDays(1))

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun betweenLocalDateAndLocalDateTime() {
        val nowTime = LocalDateTime.now()
        val nowDate = nowTime.toLocalDate()
        val actual = ChronoUnit.DAYS.between(nowTime, nowDate.plusDays(1))

        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun instantToLocalDate() {
        LocalDate.from(Instant.now().atZone(ZoneId.systemDefault()))
    }
}