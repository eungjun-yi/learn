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
}
