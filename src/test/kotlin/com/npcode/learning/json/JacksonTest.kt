package com.npcode.learning.json

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import im.toss.test.doesNotEqualTo
import im.toss.test.equalsTo
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneId
import java.time.ZonedDateTime

class JacksonTest {

    private val objectMapper = ObjectMapper().registerKotlinModule()

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

    @Test
    fun testSubtype() {
        val mapper = ObjectMapper()
        mapper.registerSubtypes(SubA1::class.java)

        SubA1("ok").let {
            mapper.readValue<SuperA>(mapper.writeValueAsString(it)).equalsTo(it)
        }

        assertThrows<InvalidTypeIdException> {
            SubA2("bad").let {
                mapper.readValue<SuperA>(mapper.writeValueAsString(it)).equalsTo(it)
            }
        }
    }

    @Test
    fun namingStrategy() {
        val mapper = ObjectMapper().registerKotlinModule()
        /*
        mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
         */

        data class Foo(
            val UPPER_CASE_NAME: String = "a",
            val mixedCaseName: String = "b"
        )

        val foo = Foo()

        println(mapper.writeValueAsString(foo))
    }

    @Test
    fun voidTest() {
        val voidHolder = ObjectMapper().registerKotlinModule().readValue<VoidHolder>("{\"value1\":1}")
    }

    @Test
    fun writePrivateFields() {
        // Jackson은 private field에 쓰려면 annotation으로 설정이 필요하다.
        objectMapper
            .readValue<PrivateFieldContainer>("{\"value\":1}")
            .toPublicFieldContainer().value shouldBe 1
    }

    @Test
    fun numberToAny() {
        with(objectMapper.readValue<Any>("1")) {
            toString() shouldBe "1"
            this::class shouldBe Int::class
        }
    }

    @Test
    fun handleMissingFields() {
        // com.fasterxml.jackson.databind.exc.MismatchedInputException: Missing required creator property 'b' (index 1)

        // Caused by: com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: Instantiation of [simple type, class com.tossbank.mci.databody.dto.전자금융사용자기본조회_CEB28010004_001_V01_Res$Dto] value failed for JSON property CSTNO due to missing (therefore NULL) value for creator parameter CSTNO which is a non-nullable type
        // at [Source: (BufferedReader); line: 1, column: 279] (through reference chain: com.tossbank.mci.telegram.telegrams.MciStandardTelegramResponse["DATAPART"]->java.util.ArrayList[0]->com.tossbank.mci.telegram.telegrams.data.DataResponsePart["DATABODY"]->com.tossbank.mci.databody.dto.전자금융사용자기본조회_CEB28010004_001_V01_Res$Dto["CSTNO"])

        objectMapper
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .readValue<TwoFieldsContainer>("{\"a\":1}").let {
                it.a shouldBe 1
                it.b shouldBe 2
            }
    }
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
interface SuperA

data class SubA1(
    val value: String = "111"
): SuperA

data class SubA2(
    val value: String = "222"
): SuperA

data class VoidHolder(
    val value1: Int,
    val value2: Nothing?
)

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
interface PrivateFieldContainer {
    val value: Int
    fun toPublicFieldContainer() = PublicFieldContainer(value)
}

data class PublicFieldContainer(
    val value: Int,
)

fun foo() {
    bar() ?: return run {
        doSomething()
    }

    // do something
}

fun bar(): String? {
    return null
}

fun doSomething(): Int = 1234
