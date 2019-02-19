package com.npcode.learning.testing

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.ZonedDateTime

class MockkPerfFasterTest {

    private val fastFoo = mockk<Foo>()
    private var startTime: ZonedDateTime? = null

    @Before
    fun init() {
        startTime = ZonedDateTime.now()
        every {
            fastFoo.bar()
        } returns 2
    }

    @Test
    fun testWithMockk() {
        // 2s 529ms
        assertThat(fastFoo.bar()).isEqualTo(2)
    }

    interface Foo {
        fun bar(): Int
    }

    @After
    fun down() {
        val between = Duration.between(startTime, ZonedDateTime.now())
        System.out.println(between.toMillis())
    }
}
