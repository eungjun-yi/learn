package com.npcode.learning.testing

import io.mockk.*
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test

class MockkTest {

    @Test
    fun test() {
        class Foo {
            fun hello()  = hello("world")
            fun hello(name: String) = "hello, $name"
        }
        val foo = spyk(Foo())
        foo.hello()
        verify {
            foo.hello("world")
            // foo.hello("me") // fail
        }
    }

    @Test
    fun `relaxed=true면 answers를 정의할 필요가 없다`() {
        val foo = mockk<Foo>(relaxed = true)

        foo.func1()

        verify {
            foo.func1()
        }
    }

    @Test
    fun `relaxed=false면 answers를 정의해야한다`() {
        val foo = mockk<Foo>(relaxed = false)

        assertThatExceptionOfType(MockKException::class.java).isThrownBy {
            foo.func1()
        }
    }

    @Test
    fun `실행 순서를 무시하는 테스트`() {
        val foo = mockk<Foo>()
        every { foo.func1() } answers { }
        every { foo.func2() } answers { }

        foo.func2()
        foo.func1()

        verify {
            foo.func1()
            foo.func2()
        }
    }

    @Test
    fun `실행 순서 테스트`() {
        val foo = mockk<Foo>()
        every { foo.func1() } answers { }
        every { foo.func2() } answers { }

        foo.func1()
        foo.func2()

        verifySequence {
            foo.func1()
            foo.func2()
        }
    }

    enum class Event {
        EVENT1, EVENT2, EVENT3
    }

    // mockk가 state machine 으로 순서를 정의할 수 있는지 불명
    @Test
    fun `복잡한 실행 순서 테스트`() {
        // AnswerMe: capture를 쓸 수 없나?

        val eventManager = mockk<EventManager>(relaxed = true)

        eventManager.start(Event.EVENT1)
        eventManager.start(Event.EVENT2)
        eventManager.end(Event.EVENT2)
        eventManager.start(Event.EVENT3)
        eventManager.end(Event.EVENT1)
        eventManager.end(Event.EVENT3)

        // 잘됨
        listOf(Event.EVENT1, Event.EVENT2, Event.EVENT3).forEach { event ->
            verifyOrder {
                eventManager.start(event)
                eventManager.end(event)
            }
        }

        // 안됨: kotlin.UninitializedPropertyAccessException: lateinit property captured has not been initialized
        val slot = slot<Event>()
        verifyOrder {
            eventManager.start(capture(slot))
            eventManager.end(slot.captured)
        }
    }

    interface Foo {
        fun func1()
        fun func2()
    }

    interface EventManager {
        fun start(event: Event)
        fun end(event: Event)
    }

    @Test
    fun `argument type 정확히 매칭하는 verify`() {
        val myService = spyk(MyService())

        myService.doSomething(MyTarget1())

        verify(exactly = 1) { myService.doSomething(ofType<MyTarget1>()) }
        verify(exactly = 0) { myService.doSomething(ofType<MyTarget2>()) }
    }

    class MyService {
        fun doSomething(target: MyTarget) { }
    }

    interface MyTarget

    class MyTarget1: MyTarget

    class MyTarget2: MyTarget
}
