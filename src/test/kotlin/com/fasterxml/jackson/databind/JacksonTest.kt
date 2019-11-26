package com.fasterxml.jackson.databind

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import im.toss.test.doesNotEqualTo
import im.toss.test.equalsTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class JacksonTest {

    @Test
    fun test() {
        val items: List<String> =
            ObjectMapper().reader().forType(List::class.java).readValue("[\"테스트1\",\"테스트2\"]")
    }

    @Test
    fun objectMapperAdjustsTimezoneIfAdjustDatesToContextTimeZoneIsNotDisabled() {
        val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

        val date: ZonedDateTime =
            objectMapper
                .reader()
                .forType(ZonedDateTime::class.java)
                .readValue("\"2018-01-01T00:00:00+09:00\"")

        assertThat(date.zone).isEqualTo(ZoneId.of("UTC"))
    }

    @Test
    fun objectMapperDoesNotAdjustTimezoneIfAdjustDatesToContextTimeZoneIsDisabled() {
        val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

        val date: ZonedDateTime =
            objectMapper
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .reader()
                .forType(ZonedDateTime::class.java)
                .readValue("\"2018-01-01T00:00:00+09:00\"")

        assertThat(date.zone).isEqualTo(ZoneId.of("+09:00"))
    }

    data class Foo(
        private val x: Int
    )

    @Test
    fun objectMapperShouldWritePrivateField() {
        val objectMapper = ObjectMapper().registerKotlinModule()

        val foo1 = objectMapper.readValue<Foo>("{\"x\": 1}")
        val foo2 = objectMapper.readValue<Foo>("{\"x\": 1}")
        val foo3 = objectMapper.readValue<Foo>("{\"x\": 2}")

        foo1.equalsTo(foo2)
        foo1.doesNotEqualTo(foo3)
    }
}
