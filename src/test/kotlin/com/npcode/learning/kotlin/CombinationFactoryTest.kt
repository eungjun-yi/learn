package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombinationFactoryTest {
    data class Foo(val a: String)

    @Test
    fun testEmpty() {
        val foo: Foo = EmptyFactory.dummy()
        assertThat(foo).isEqualTo(Foo(""))
    }

    @Test
    fun testCombinationString() {
        val foos: Set<Foo?> = CombinationFactory.combination()
        assertThat(foos).containsAll(
            listOf(
                Foo("")
            )
        )
    }

    @Test
    fun testCombinationBoolean() {
        data class Foo(val a: Boolean)
        val foos: Set<Foo?> = CombinationFactory.combination()
        assertThat(foos).containsAll(
            listOf(
                Foo(true),
                Foo(false)
            )
        )
    }

    @Test
    fun testCombinationNullableBoolean() {
        data class Foo(val a: Boolean?)
        val foos: Set<Foo?> = CombinationFactory.combination()
        assertThat(foos).containsAll(
            listOf(
                Foo(null),
                Foo(true),
                Foo(false)
            )
        )
    }

    @Test
    fun testCombinationInt() {
        data class Foo(val a: Int)
        val foos: Set<Foo?> = CombinationFactory.combination()
        assertThat(foos).containsAll(
            listOf(
                Foo(-1),
                Foo(0),
                Foo(1)
            )
        )
    }

    @Test
    fun testCombinationIntAndBoolean() {
        data class Foo(val a: Int, val b: Boolean)
        val foos: Set<Foo?> = CombinationFactory.combination()
        assertThat(foos).containsAll(
            listOf(
                Foo(-1, true),
                Foo(0, true),
                Foo(1, true),
                Foo(-1, false),
                Foo(0, false),
                Foo(1, false)
            )
        )
    }

    /*
    @Test
    fun testCombinationWithGivenGenerator() {
        data class Foo(val a: Int)
        val generators = mapOf(Foo::a to listOf(1, 2, 3))
        val foos: Set<Foo?> = CombinationFactory.combination(generators = generators)
        assertThat(foos).containsAll(
            listOf(
                Foo(1),
                Foo(2),
                Foo(3)
            )
        )
        // 특정 parameter에 generator를 지정할 수 있을까?
    }
     */
    data class Foo2(
        val a: Boolean,
        val b: Boolean,
        val c: Boolean
    )

    fun isThisGood(foo: Foo2) = foo.a

    @Test
    fun testCombinationByGenerator() {

        // isThisGood의 여부는 Foo2.b나 Foo2.c 와는 무관하게 Foo2.a 의 여부만으로 결정된다는 테스트

        combination<Foo2> {
            Foo2(true, anyBoolean(), anyBoolean())
        }.map { it: Foo2 ->
            assertThat(isThisGood(it)).isTrue()
        }

        combination<Foo2> {
            Foo2(false, anyBoolean(), anyBoolean())
        }.map {
            assertThat(isThisGood(it)).isFalse()
        }
    }

    @Test
    fun testCombinationByGenerator2() {

        val foos: Set<Foo2> = combination {
            Foo2(true, anyBoolean(), anyBoolean())
        }

        assertThat(foos).containsAll(
            listOf(
                Foo2(true, true, true),
                Foo2(true, true, false),
                Foo2(true, false, true),
                Foo2(true, false, false)
            )
        )
    }

    @Test
    fun testCombinationByGenerator3() {

        data class Foo(
            val a: Boolean,
            val b: Boolean,
            val c: Int,
            val d: Int
        )

        val foos: Set<Foo> = combination {
            Foo(true, anyBoolean(), anyInt(1..2), anyInt(listOf(3, 5, 8)))
        }

        assertThat(foos).containsAll(
            listOf(
                Foo(true, true, 1, 3),
                Foo(true, true, 1, 5),
                Foo(true ,true, 1, 8),
                Foo(true ,true, 2, 3),
                Foo(true ,true, 2, 5),
                Foo(true ,true, 2, 8),
                Foo(true, false, 1, 3),
                Foo(true, false, 1, 5),
                Foo(true ,false, 1, 8),
                Foo(true ,false, 2, 3),
                Foo(true ,false, 2, 5),
                Foo(true ,false, 2, 8)
            )
        )
    }

    enum class Bar {
        A, B
    }

    @Test
    fun testCombinationByGenerator4() {

        data class Foo(
            val a: Bar
        )

        val foos: Set<Foo> = combination {
            Foo(Bar.A)
        }

        assertThat(foos).containsAll(
            listOf(
                Foo(Bar.A)
            )
        )
    }
}
