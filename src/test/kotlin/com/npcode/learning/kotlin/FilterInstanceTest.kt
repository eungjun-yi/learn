package com.npcode.learning.kotlin

import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test

class FilterInstanceTest {

    @Test
    fun `filterIsInstance는 type argument로 필터링하지 못한다`() {
        listOf(1, "2").filterIsInstance<String>() shouldHaveSize 1
        listOf(Foo<String>(), Foo<Int>()).filterIsInstance<Foo<Int>>() shouldHaveSize 2
    }

    class Foo<T>
}
