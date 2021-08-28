package com.npcode.learning.json

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import im.toss.test.doesNotEqualTo
import im.toss.test.equalsTo
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneId
import java.time.ZonedDateTime

class GsonTest {

    @Test
    fun writePrivateFields() {
        // GSON은 private field에 잘 쓴다.
        Gson().fromJson("{\"value\":1}", PrivateFieldContainer::class.java)
            .toPublicFieldContainer().value shouldBe 1
    }

    @Test
    fun handleMissingFields() {
        with(Gson().fromJson("{\"a\":1}", TwoFieldsContainer::class.java)) {
            a shouldBe 1
            b shouldBe 0 // gson은 디폴트값을 무시해버린다.
            // c shouldBe "" // gson은 디폴트값을 무시해버린다.
        }
    }

    @Test
    fun readNumber() {
        Gson().fromJson("1", Any::class.java).toString() shouldBe "1.0"
    }
}

data class TwoFieldsContainer(
    val a: Int,
    val b: Int,
    val c: String = "3",
)
