package com.npcode.learning.kotlin

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class MockkPerfTest {

    @Test
    fun testWithoutMockingFramework() {
        val foo = object: Foo {
            override fun bar() = 2
        }

        // 86ms
        assertThat(foo.bar()).isEqualTo(2)
    }

    @Test
    fun testWithMockito() {
        val foo = Mockito.mock(Foo::class.java)
        `when`(foo.bar()).thenReturn(2)

        // 619ms
        assertThat(foo.bar()).isEqualTo(2)
    }

    @Test
    fun testWithMockitoKotlin() {
        val foo = mock<Foo> {
            on { bar() } doReturn 2
        }

        // 828ms
        assertThat(foo.bar()).isEqualTo(2)
    }

    @Test
    fun testWithMockk() {
        val foo = mockk<Foo>()
        every {
            foo.bar()
        } returns 2

        // 2s 529ms
        assertThat(foo.bar()).isEqualTo(2)
    }

    interface Foo {
        fun bar(): Int
    }
}
