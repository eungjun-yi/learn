package com.npcode.learning.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.GsonBuilder
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class SerializeDelegationTest{
    private val jackson = ObjectMapper().registerKotlinModule()
    private val gson = GsonBuilder().create()
    private val json = "{\"a\":\"b\",\"b\":\"c\"}"

    @Test
    fun testDeserialize() {
        // gson으로 위임된 클래스의 역직렬화가 가능한 경우가 일부 있다.
        gson.fromJson(json, Foo::class.java).b shouldBe "c"
        gson.fromJson(json, Bar::class.java).b shouldBe "c"
        gson.fromJson(json, Delegated3::class.java).b shouldBe "c"
        gson.fromJson(json, Delegated4::class.java).b shouldBe "c"

        // jackson은 위임된 클래스의 역직렬화를 하지 못한다.
        jackson.readValue<Foo>(json).b shouldBe "c"
        jackson.readValue<Bar>(json).b shouldBe "c"

        // kotlinx는 위임된 클래스의 역직렬화를 하지 못한다.
        Json.decodeFromString<Foo>(json).b shouldBe "c"
        Json.decodeFromString<Bar>(json).b shouldBe "c"

        // 실패
        // gson.fromJson("{\"a\":\"b\",\"b\":\"c\"}", Baz::class.java).b shouldBe "c"
        // jackson.readValue<Baz>(json).b shouldBe "c"
        // jackson.readValue<Baz2>(json).b shouldBe "c"
        // jackson.readValue<Baz4>(json).b shouldBe "c"
        // Json.decodeFromString<Delegated2>(json).b shouldBe "c"
    }

    @Test
    fun testSerialize() {
        // jackson으로 위임된 클래스의 직렬화가 가능한 경우가 일부 있다.
        jackson.writeValueAsString(Delegated1(object: BarView { override val b = "c"}, "b")) shouldBe json
        jackson.writeValueAsString(Delegated2(object: BarView { override val b = "c"}, "b")) shouldBe json

        // gson, kotlinx는 위임된 클래스의 직렬화를 하지 못한다.

        // 실패
        // gson.toJson(Foo("a", "b"))
        // gson.toJson(Bar("a", "b"))
        // gson.toJson(Baz(object: BarView { override val b = "c"}, "b")) shouldBe json
        // gson.toJson(Baz3(object: BarView { override val b = "c"}, "b")) shouldBe json
        // jackson.writeValueAsString(Baz2(object: BarView { override val b = "c"}, "b")) shouldBe json
        // jackson.writeValueAsString(Baz3(object: BarView { override val b = "c"}, "b")) shouldBe json
        // Json.encodeToString(Delegated1(object: BarView { override val b = "c"}, "b")) shouldBe json
        // Json.encodeToString(Delegated2(object: BarView { override val b = "c"}, "b")) shouldBe json
    }
}

@Serializable
data class Foo(
    val a: String
) {
    val b: String = ""
}

interface BarView {
    val b: String
}

@Serializable
data class Bar(
    val a: String
): BarView {
    override val b: String = ""
}

class Delegated1(
    delegation: BarView,
    val a: String
): BarView by delegation

@Serializable
data class Delegated2(
    private val delegation: BarView,
    val a: String
): BarView by delegation

@Serializable
data class Delegated3(
    private val delegation: BarView,
    val a: String
): BarView by delegation {
    override val b = ""
}

class Delegated4(
    delegation: BarView,
    val a: String
): BarView by delegation {
    override val b = ""
}
