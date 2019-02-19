package com.npcode.learning.reactor
import mu.KotlinLogging
import org.junit.Test
import org.slf4j.MDC
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.util.context.Context

class ReactorLogByEventTest {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    class EventListener(private val context: Context) {
        // 발생한 위치를 기록하기
        fun notifyEvent1() {
            val pos = Thread.currentThread().stackTrace[2]
            log(context) {
                logger.info("Event1 occurred at $pos")
            }
        }

        fun notifyEvent2() {
            val pos = Thread.currentThread().stackTrace[2]
            log(context) {
                logger.info("Event2 occurred at $pos")
            }
        }
    }

    class SomeDomainService(private val listener: EventListener) {
        fun doSomething(it: String): String {
            // context는 Mono나 Flux에 담겨있어야하며 여기서는 결코 context를 알 수 없다. 만약 여기서 로깅을 하려고 한다면
            // 이것은 bean이 아니라 매번 생성하는 것이 타당하다.
            // Do something and publish event
            listener.notifyEvent1()
            // Do Something
            val result = it.toUpperCase()
            listener.notifyEvent2()
            return result
        }
    }

    @Test
    fun testEventLog() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    listOf("a", "b", "c").toFlux()
                        .map {
                            SomeDomainService(EventListener(context)).doSomething(it)
                        }
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            .blockLast()
    }
}

fun log(context: Context, logStatement: () -> Unit) {
    val userNo: Long = context["userNo"]
    MDC.putCloseable("userNo", userNo.toString()).use {
        logStatement.invoke()
    }
}
