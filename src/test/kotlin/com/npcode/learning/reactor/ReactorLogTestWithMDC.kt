package com.npcode.learning.reactor
import mu.KLogger
import mu.KotlinLogging
import org.junit.Test
import org.slf4j.MDC
import reactor.core.publisher.*
import reactor.util.context.Context
import java.time.Duration
import java.util.function.Consumer
import kotlin.random.Random

class ReactorLogTestWithMDC {

    @Test
    fun doOnEachBefore() {
        listOf(1, 2, 3).toFlux()
            .doOnEach(mdcUpdater())
            .doOnNext { logger.info("Print hello message") }
            .map { System.out.println("Hello, $it") }
            // 아래 줄은 반드시 doOnEach 보다 나중이어야한다.
            .subscriberContext {
                it.put("userNo", 123).put("foo", "bar2")
            }
            .log()
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .blockLast()
    }

    companion object {
        private val logger = KotlinLogging.logger {}

        fun <T> mdcUpdater(): Consumer<Signal<T>> {
            return Consumer { signal ->
                // onNext시에만 동작하려면 애초에 doOnNext()에서 호출해도 되었을 것 같지만,
                // 어째선지 doOnNext()에서는 context를 얻을 수 없다.
                if (signal.isOnNext) {
                    updateMdc(signal.context)
                }
            }
        }

        private fun updateMdc(context: Context) {
            val userNo: Int? = context.get("userNo")
            userNo?.let {
                logger.info { "update mdc" }
                MDC.put("userNo", it.toString())
            }
        }

        private fun clearMdc() {
            MDC.clear()
        }

        fun mdcUpdaterUserNo(userNo: Int) {
            MDC.put("userNo", userNo.toString())
        }
    }

    @Test
    fun doOnEachAfter() {
        listOf(1, 2, 3).toFlux()
            .doOnNext { logger.info("Print hello message") }
            .map { System.out.println("Hello, $it") }
            .log()
            .doOnEach(mdcUpdater())
            // 아래 줄은 반드시 doOnEach 보다 나중이어야한다.
            .subscriberContext {
                it.put("userNo", 123).put("foo", "bar2")
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .blockLast()
    }

    @Test
    fun updateMdc() {
        listOf(1, 2, 3).toFlux()
            .map {
                MDC.put("userNo", it.toString())
                it
            }
            .flatMap {
                listOf("a", "b", "c").toFlux()
                    .map { logger.info("Hello, $it") }
                    .log()
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .blockLast()
    }

    @Test
    fun updateMdcByContext() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                listOf("a", "b", "c").toFlux()
                    .doOnEach(mdcUpdater())
                    .map {
                        logger.info("Hello, $it")
                    }
                    .log()
                    .collectList()
                    .subscriberContext {
                        it.put("userNo", userNo)
                    }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .blockLast()
    }

    @Test
    fun updateMdcOnlyIfLog() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    listOf("a", "b", "c").toFlux()
                        .map {
                            updateMdc(context)
                            logger.info("Hello, $it")
                            clearMdc()
                        }
                        .log()
                        .collectList()
                        .subscriberContext(context)
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .blockLast()
    }

    @Test
    fun innerPublishers() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                listOf("a", "b", "c").toFlux()
                    // .doOnEach(mdcUpdater()) // 여기에만 mdcUpdater를 주면 제대로 userNo가 기록되지 않는다
                    .flatMap { from ->
                        Flux.range(0, 100)
                            .doOnEach(mdcUpdater())
                            .map { to ->
                                logger.info("[${Thread.currentThread().id}] User $userNo moves $from to $to")
                                to
                            }.delayElements(Duration.ofMillis(Random.nextLong(1, 10)))
                    }
                    .log()
                    .delayElements(Duration.ofMillis(Random.nextLong(1, 10)))
                    .collectList()
                    .subscriberContext {
                        it.put("userNo", userNo)
                    }
            }
            // 멀티스레드로 동작하게 하려고
            .blockLast()
    }

    fun <T> log(signal: Signal<T>) {
        updateMdc(signal.context)
        logger.info("Hello, ${signal.get()}")
        clearMdc()
    }

    @Test
    fun updateMdcOnlyIfLog2() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap {
                    listOf("a", "b", "c").toFlux()
                        .doOnEach { log(it) }
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .blockLast()
    }

    // 책에서 보았던 "로그 자체를 분리하기"에 대해
    // 모든 로그를 알림으로 바꾼다

    @Test
    fun logInMap() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap {
                    listOf("a", "b", "c").toFlux()
                        .flatMap { name ->
                            logAndMap(
                                { logger.info("Hello, $name") },
                                { name + name }
                            )
                        }
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .blockLast()
    }

    private fun <T> logAndMap(
        log: () -> Unit,
        map: () -> T
    ): Mono<T> {
        return Mono.subscriberContext().map {
            updateMdc(it)
            log.invoke()
            clearMdc()
        }.map {
            map.invoke()
        }
    }

    fun <T> logOnNext(logStatement: (T) -> Unit) =
        { signal: Signal<T> ->
            if (signal.type == SignalType.ON_NEXT) {
                val userNo: Long = signal.context["userNo"]
                MDC.putCloseable("userNo", userNo.toString()).use {
                    logStatement.invoke(signal.get()!!)
                }
            }
        }

    fun <T> logOnError(logStatement: (Throwable) -> Unit) =
        { signal: Signal<T> ->
            val context = signal.context
            val throwable = signal.throwable
            if (signal.type == SignalType.ON_ERROR) {
                logError(context, logStatement, throwable!!)
            }
        }

    private fun logError(
        context: Context,
        logStatement: (Throwable) -> Unit,
        throwable: Throwable
    ) {
        val userNo: Long = context["userNo"]
        MDC.putCloseable("userNo", userNo.toString()).use {
            logStatement.invoke(throwable)
        }
    }

    @Test
    fun logInErrorContinue() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    listOf("a", "b", "c").toFlux()
                        .map {
                            if (it == "b") throw RuntimeException()
                            it
                        }
                        .onErrorContinue { error, value ->
                            updateMdc(context)
                            logger.info("error!", error)
                            clearMdc()
                        }
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .map {
                System.out.println(it)
            }
            .blockLast()
    }

    @Test
    fun logOnNext() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    listOf("a", "b", "c").toFlux()
                        .doOnEach(logOnNext { name -> logger.info("hello, $name") })
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .map {
                System.out.println(it)
            }
            .blockLast()
    }

    @Test
    fun logOnError() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    listOf("a", "b", "c").toFlux()
                        .map {
                            logger.info("Checking $it")
                            if (it == "b") throw RuntimeException()
                            it
                        }
                        .doOnEach(logOnError { err -> logger.info("error", err) })
                        // .onErrorContinue { t, u -> } // 이게 있으면 로그가 안 남는다
                        // .onErrorResume { Mono.empty() } // 이게 있으면 c는 결코 도달하지 못한다.
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .map {
                System.out.println(it)
            }
            .blockLast()
    }

    @Test
    fun logOnErrorContinue() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    listOf("a", "b", "c").toFlux()
                        .map {
                            logger.info("Checking $it")
                            if (it == "b") throw RuntimeException()
                            it
                        }
                        .onErrorContinue { t, u ->
                            logError(context, { err -> logger.info("error", err) }, t)
                        }
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .map {
                System.out.println(it)
            }
            .blockLast()
    }

    @Test
    fun logInCaseOfBothOfOkAndError() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    listOf("a", "b", "c").toFlux()
                        .map {
                            if (it == "b") throw RuntimeException()
                            it
                        }
                        .doOnEach(logOnNext { logger.info("Handling $it") }) // Handling b는 기록 안됨. 에러라서
                        .onErrorContinue { t, u ->
                            logError(context, { err -> logger.info("error", err) }, t)
                        }
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .map {
                System.out.println(it)
            }
            .blockLast()
    }

    @Test
    fun testContextLogger() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    val logger = ContextAwareLogger(context, KotlinLogging.logger({}))
                    listOf("a", "b", "c").toFlux()
                        .map {
                            if (it == "b") throw RuntimeException()
                            it
                        }
                        .doOnNext {
                            logger.info("Handling $it")
                        }
                        // .doOnEach(logOnNext { logger.info("Handling $it") }) // Handling b는 기록 안됨. 에러라서
                        .onErrorContinue { t, u ->
                            logError(context, { err -> logger.info("error", err) }, t)
                        }
                        .log(logger)
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .map {
                System.out.println(it)
            }
            .blockLast()
    }

    // logger는 한번 설정하면 계속 따라가므로 가장 밖에서만 넣어주면 된다.
    @Test
    fun testNestedPublisher() {
        listOf(1, 2, 3).toFlux()
            .flatMap { userNo ->
                Mono.subscriberContext().flatMap { context ->
                    val logger = ContextAwareLogger(context, KotlinLogging.logger({}))
                    listOf("a", "b", "c").toFlux()
                        .flatMap { str ->
                            // 저 logger를 그냥 쓸 수 있다면 정말 좋겠다.
                            Flux.just(1, 2, 3).map { str + it }
                        }
                        .doOnNext {
                            logger.info("Handling $it")
                        }
                        // .doOnEach(logOnNext { logger.info("Handling $it") }) // Handling b는 기록 안됨. 에러라서
                        .onErrorContinue { t, u ->
                            logError(context, { err -> logger.info("error", err) }, t)
                        }
                        .log(logger)
                        .collectList()
                }.subscriberContext {
                    it.put("userNo", userNo)
                }
            }
            // 멀티스레드로 동작하게 하려고
            .delayElements(Duration.ofMillis(1))
            .map {
                System.out.println(it)
            }
            .blockLast()
    }

    class ContextAwareLogger(private val context: Context, private val baseLogger: KLogger): reactor.util.Logger, KLogger by KotlinLogging.logger({}) {
        override fun info(msg: String) {
            info2 { baseLogger.info(msg) }
        }

        override fun info(format: String, vararg arguments: Any?) {
            info2 { baseLogger.info(format, arguments) }
        }

        private fun info2(logStatement: () -> Unit) {
            MDC.putCloseable("userNo", context.get<Long>("userNo").toString()).use {
                logStatement.invoke()
            }
        }
    }

}
