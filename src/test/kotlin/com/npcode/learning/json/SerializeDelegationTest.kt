package com.npcode.learning.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.GsonBuilder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SerializeDelegationTest{
    private val jackson = ObjectMapper().registerKotlinModule()
    private val gson = GsonBuilder().create()
    private val json = "{\"a\":\"b\",\"b\":\"c\"}"

    @Test
    fun testDeserialize() {
        gson.fromJson(json, Foo::class.java).b shouldBe "c"
        gson.fromJson(json, Bar::class.java).b shouldBe "c"
        gson.fromJson(json, Delegated3::class.java).b shouldBe "c"
        gson.fromJson(json, Delegated4::class.java).b shouldBe "c"

        jackson.readValue<Foo>(json).b shouldBe "c"
        jackson.readValue<Bar>(json).b shouldBe "c"

        // 실패
        // gson.fromJson("{\"a\":\"b\",\"b\":\"c\"}", Baz::class.java).b shouldBe "c"
        // jackson.readValue<Baz>(json).b shouldBe "c"
        // jackson.readValue<Baz2>(json).b shouldBe "c"
        // jackson.readValue<Baz4>(json).b shouldBe "c"
    }

    @Test
    fun testSerialize() {
        jackson.writeValueAsString(Delegated1(object: BarView { override val b = "c"}, "b")) shouldBe json
        jackson.writeValueAsString(Delegated2(object: BarView { override val b = "c"}, "b")) shouldBe json

        // 실패
        // gson.toJson(Foo("a", "b"))
        // gson.toJson(Bar("a", "b"))
        // gson.toJson(Baz(object: BarView { override val b = "c"}, "b")) shouldBe json
        // gson.toJson(Baz3(object: BarView { override val b = "c"}, "b")) shouldBe json
        // jackson.writeValueAsString(Baz2(object: BarView { override val b = "c"}, "b")) shouldBe json
        // jackson.writeValueAsString(Baz3(object: BarView { override val b = "c"}, "b")) shouldBe json
    }
}

data class Foo(
    val a: String
) {
    val b: String = ""
}

interface BarView {
    val b: String
}

data class Bar(
    val a: String
): BarView {
    override val b: String = ""
}

class Delegated1(
    delegation: BarView,
    val a: String
): BarView by delegation

data class Delegated2(
    private val delegation: BarView,
    val a: String
): BarView by delegation

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
