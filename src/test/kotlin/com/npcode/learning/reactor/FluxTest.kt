package com.npcode.learning.reactor
import im.toss.test.equalsTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.Disposables
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import reactor.test.test
import java.time.Duration
import java.util.logging.Level

class FluxTest {
    @Test
    fun concatMultipleFlux() {
        val a = Flux.fromIterable(listOf(1, 3, 5))
        val b = Flux.fromIterable(listOf(2, 4, 8))

        a.concatWith(b).map { System.out.println(it) }
    }

    @Test
    fun noSubscribe() {
        val a = listOf(1, 2, 3).toFlux()
        a.map { System.out.println(it) } // 아무것도 출력하지 않음
    }

    @Test
    fun subscribe() {
        val a = listOf(1, 2, 3).toFlux()
        a.map { System.out.println(it) }.subscribe() // 1, 2, 3을 출력함
    }

    @Test
    fun dispose() {
        val a = Flux.just(1, 2, 3)
        // 1, 2, 3을 출력함. subscribe() 호출되었을 때 FluxArray의 순회가 끝난다.
        // flux async mode로 실행하는 법은 모르겠다.
        a.subscribe { System.out.println(it) }.dispose()
    }

    @Test
    fun composite() {
        val a = Flux.just(1, 3, 5).subscribe()
        val b = Flux.just(2, 4, 6).subscribe()
        Disposables.composite(a, b).dispose()
    }

    @Test
    fun generate() {
        // FluxGenerator를 만든다.
        val flux: Flux<String> = Flux.generate({
            0
        }, { state, sink ->
            sink.next("state: " + state)
            if (state == 10) sink.complete()
            state + 1
        })

        flux.subscribe { System.out.println(it) }
    }

    interface MyEventListener {
        fun onData(data: String)
        fun processComplete()
    }

    class MyEventProcessor {

        lateinit var listener: MyEventListener

        fun register(listener: MyEventListener) {
            this.listener = listener
        }

        fun emit(data: String) {
            listener.onData(data)
        }

        fun done() {
            listener.processComplete()
        }
    }

    @Test
    fun push() {
        // 이것도 마찬가지로 subscribe을 해야 동작하기 시작함.
        val processor = MyEventProcessor()
        // FluxCreate를 만든다.
        val flux = Flux.push<String> { sink ->
            processor.register(object: MyEventListener {
                override fun processComplete() {
                    sink.complete()
                }

                override fun onData(data: String) {
                    System.out.println("onData: $data")
                    sink.next(data)
                }
            })

            sink.onRequest {
                System.out.println("on request")
                sink.next("next")
                // Do nothing
            }
        }
        // return onAssembly(new FluxCreate<>(emitter, OverflowStrategy.BUFFER, FluxCreate.CreateMode.PUSH_ONLY));
        System.out.println("before subscribe")
        // processor.emit("hello") // error: lateinit property listener has not been initialized
        flux.log().subscribe()
        System.out.println("after subscribe")
        processor.emit("hello")
        processor.emit("bye")
    }

    // Flux.pull 이라는 건 없다. create_mode는 PUSH_ONLY와 PUSH_PULL 두 가지다. Flux.create 하면 PUSH_PULL 모드로 생성된다.

    @Test
    fun create() {
        // request를 보내야만 쏘는걸까?
        // Flux.create()
        // return onAssembly(new FluxCreate<>(emitter, OverflowStrategy.BUFFER, FluxCreate.CreateMode.PUSH_PULL));
        val processor = MyEventProcessor()
        // FluxCreate를 만든다.
        val flux = Flux.create<String> { sink ->
            processor.register(object: MyEventListener {
                override fun processComplete() {
                    sink.complete()
                }

                override fun onData(data: String) {
                    System.out.println("onData: $data")
                    sink.next(data)
                }
            })

            sink.onRequest {
                System.out.println("on request")
                sink.next("next")
                // Do nothing
            }
        }
        System.out.println("before subscribe")
        // processor.emit("hello") // error: lateinit property listener has not been initialized
        flux.log().subscribe()
        System.out.println("after subscribe")
        processor.emit("hello")
        processor.emit("bye")
        // push와의 차이점을 전혀 모르겠다.
    }

    @Test
    fun shuffle() {
        val x = Flux.just(1, 2, 3).collectList().flatMapIterable { it.shuffled() }.take(1).blockFirst()
        System.out.println(x)
    }

    @Test
    fun newParallel() {
        listOf(1, 2, 3).toFlux().map {
            System.out.println(listOf(4, 5, 6).toFlux().blockFirst())
        }.subscribeOn(Schedulers.newParallel("new-parallel-test", 2))
            .blockFirst()
        // thread가  NonBlockingThread 면 이런 에러가 난다:
        // java.lang.IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread new-parallel-test-1
    }

    @Test
    fun newParallel2() {
        // ReactorThreadFactory 는 package private이라서 만들기가 매우 어렵다.
        /*
        listOf(1, 2, 3).toFlux().map {
            System.out.println(listOf(4, 5, 6).toFlux().blockFirst())
        }.subscribeOn(
            Schedulers.newParallel(2,
            ReactorThreadFactory(
                "new-parallel-test",
                COUNTER,
                false,
                true,
                Schedulers::defaultUncaughtException)
            )
        )
            .blockFirst()
            */

    }

    @Test
    fun skipIfError() {
        Flux.just(1, 0, 2)
            .map { 10 / it }
            // .onErrorResume { Flux.empty() }
            .onErrorContinue { throwable: Throwable, any: Any -> }
            .collectList()
            .map { System.out.println("result: $it") }
            .block()
    }

    class FirstLevelException(msg: String, e: Throwable): RuntimeException(msg, e)

    class SecondLevelException(msg: String, e: Throwable): RuntimeException(msg, e)

    @Test
    fun handleError() {
        Mono.fromCallable { throw RuntimeException() }
            .onErrorMap { FirstLevelException("first", it)  }
            .onErrorMap { SecondLevelException("second", it)  }
            .block()
    }

    @Test
    fun logOnFlatMap() {
        // flatmap은 이렇게 쉽게 context가 전달되지만
        Mono.just(1).flatMap {
            Mono.just(1).doOnEach {
                if (it.isOnNext) {
                    System.out.println(it.context.get("foo") as Int)
                }
            }
        }.subscriberContext { it.put("foo", 123)  }.block()
    }

    @Test
    fun logOnMap() {
        // map은 subscrierContext가 필요하다
        Mono.subscriberContext().map { context ->
            Mono.just(1).doOnEach {
                if (it.isOnComplete) {
                    println(it.context.get("foo") as Int)
                }
            }.subscriberContext(context).block()
        }.subscriberContext { it.put("foo", 123)  }.block()
    }

    @Test
    fun logOnlyIfSuccess() {
        Mono.subscriberContext().map { context ->
            Flux.just(1, 2, 0)
                .map { 1 / it }
                .doOnEach {
                    println("${it.type} foo: ${it.context.get("foo") as Int}")
                }.subscriberContext(context).blockLast()
        }.subscriberContext { it.put("foo", 123)  }.block()
    }

    @Test
    fun collectListShouldDropEmptyMono() {
        val flux = Flux.just(1, 2, 3).flatMap {
            if (it % 2 == 0) Mono.just(it) else Mono.empty()
        }
            .collectList()

        StepVerifier
            .create(flux)
            .expectNext(listOf(2))
            .verifyComplete()
    }

    @Test
    fun excludeByNestedMap() {
        val flux1 = Flux.just(1, 2, 3)
        val flux2 = Flux.just(2, 4)

        // how to exclude flux2 from flux1
        val result = flux1.collectList().flatMap { list1 ->
            flux2.collectList().map { list2 ->
                list1 - list2
            }
        }

        StepVerifier
            .create(result)
            .expectNext(listOf(1, 3))
            .verifyComplete()
    }

    @Test
    fun excludeByZip() {
        val flux1 = Flux.just(1, 2, 3)
        val flux2 = Flux.just(2, 4)

        val mono1 = flux1.collectList()
        val mono2 = flux2.collectList()

        val result = mono1.zipWith(mono2).flatMapIterable { it.t1 - it.t2 }

        listOf(1) - listOf(2)

        StepVerifier
            .create(result)
            .expectNext(1)
            .expectNext(3)
            .verifyComplete()
    }

    @Test
    fun sort() {
        val flux = Flux.just(2, 1, 3)

        val sorted = flux.sort { o1, o2 -> o1.compareTo(o2)  }

        StepVerifier
            .create(sorted)
            .expectNext(1, 2, 3)
            .verifyComplete()
    }

    @Test
    fun reversedSort() {
        val flux = Flux.just(2, 1, 3)

        val sorted = flux.sort { o1, o2 -> o2.compareTo(o1)  }

        StepVerifier
            .create(sorted)
            .expectNext(3, 2, 1)
            .verifyComplete()
    }

    @Test
    fun sort2() {
        val flux = Flux.just(2, 1, 3)

        val sorted = flux.sort(Integer::compare)

        StepVerifier
            .create(sorted)
            .expectNext(1, 2, 3)
            .verifyComplete()
    }

    @Test
    fun toStream() {
        // * Note that iterating from within threads marked as "non-blocking only" is illegal and will
        // * cause an {@link IllegalStateException} to be thrown, but obtaining the {@link Stream}
        // * itself or applying lazy intermediate operation on the stream within these threads is ok.
        val stream = Flux.just(1 ,2, 3).toStream()
    }

    @Test
    fun onErrorContinue() {
        Flux.just(1, 0, 2)
            .map { 2 / it }
            .onErrorContinue { t, u -> }
            .collectList()
            .test()
            .expectNext(listOf(2, 1))
            .verifyComplete()
    }

    @Test
    fun onErrorContinue2() {
        Flux.error<Int>(RuntimeException())
            .onErrorContinue { t, u ->
            }
            .collectList()
            .test()
            .verifyComplete()
    }

    @Test
    fun onErrorResume() {
        Flux.concat(Flux.just(1), Flux.error<Int>(RuntimeException()), Flux.just(3))
            .collectList()
            .onErrorResume { Mono.empty() }
            .test()
            .verifyComplete()
    }

    @Test
    fun onErrorResume2() {
        Flux.error<Int>(RuntimeException())
            .onErrorResume { Flux.empty() }
            .collectList()
            .test()
            .expectNext(listOf())
            .verifyComplete()
    }

    @Test
    fun onErrorContinue3() {
        Flux.concat(Flux.just(1), Flux.error<Int>(RuntimeException()), Flux.just(3))
            .onErrorContinue(java.lang.RuntimeException::class.java) { t, u -> }
            .test()
            .expectNext(1)
            .expectNext(3)
            .verifyComplete() // fail
    }

    @Test
    fun onErrorContinue5() {
        Flux.generate<Int> { throw RuntimeException() }
            .onErrorContinue { t, u -> }
            .take(1)
            .test()
            .verifyComplete() // fail
    }

    @Test
    fun onErrorContinue7() {
        Flux.just(0)
            .map { throw RuntimeException() }
            .onErrorContinue { t, u -> }
            .test()
            .verifyComplete()
    }

    @Test
    fun onErrorContinue8() {
        Flux.just(0)
            .flatMap { Flux.error<Int>(java.lang.RuntimeException()) }
            .onErrorContinue { t, u -> }
            .test()
            .verifyComplete() // fail
    }

    @Test
    fun onErrorResume3() {
        Flux.just(0)
            .flatMap { Flux.error<Int>(java.lang.RuntimeException()) }
            .onErrorResume { Flux.empty() }
            .test()
            .verifyComplete()
    }

    @Test
    fun onErrorResume4() {
        Flux.just(0)
            .map { throw RuntimeException() }
            .onErrorResume { Flux.empty() }
            .test()
            .verifyComplete()
    }

    @Test
    fun retry() {
        Flux.concat(Flux.just(1), Flux.error<Int>(RuntimeException()), Flux.just(3))
            .retry()
            .test()
            .expectNext(1)
            .expectNext(1)
            .verifyComplete()
    }

    private val logger = object : reactor.util.Logger, Logger by LoggerFactory.getLogger("mytest") {}

    @Test
    fun log() {
        // 디버깅을 해 보면, 여기서 INFO로 설정을 했더라도 onError 등의 에러 관련 로그는 에러 레벨 로그로 남긴다.
        // Signal.onErrorCall()이 이 로그 레벨을 무시한다.
        Flux.concat(Flux.just(1), Flux.error<Int>(RuntimeException()), Flux.just(3))
            .log(logger, Level.INFO, true)
            .test()
            .expectNext(1)
            .expectError(java.lang.RuntimeException::class.java)
            .verify()
    }

    @Test
    fun collectList() {
        Mono.empty<Int>().toFlux().collectList().test().expectNext(emptyList()).verifyComplete()
    }

    @Test
    fun cache() {
        // 캐시 대상 publisher가 cached1 출력을 포함하므로 cached1 한번만 출력
        val mono1 = Mono.just(1).doOnNext { System.out.println("cached1") }.cache()
        mono1.block()
        mono1.block()

        // 캐시 대상 publisher가 cached2 출력을 포함하지 않으므로 cached2 두 번 출력
        val mono2 = Mono.just(1).cache().doOnNext { System.out.println("cached2") }
        mono2.block()
        mono2.block()
    }

    @Test
    fun `Operator called default onErrorDropped`() {
        val create = Flux.create<String> { sink ->
            sink.error(RuntimeException())
            sink.complete()
        }
        create.test().verifyComplete()
    }

    @Test
    fun `filterWhen은 publisher를 받는다`() {
        Flux.just(1, 2, 3)
            .filterWhen { (it > 1).toMono() }
            .test()
            .expectNext(2)
            .expectNext(3)
            .verifyComplete()
    }

    @Test
    fun `filterWhen이 Mono empty 라면 false 처럼 처리되는 듯`() {
        Flux.just(1, 2, 3)
            .filterWhen { Mono.empty() }
            .test()
            .verifyComplete()
    }

    @Test
    fun `빈 flux라면 all은 true`() {
        Flux.empty<Int>().all {
            it == 1
        }.test().expectNext(true).verifyComplete()
    }

    @Test
    fun `빈 flux라면 any는 false`() {
        Flux.empty<Int>().any {
            it == 0
        }.test().expectNext(false).verifyComplete()
    }

    @Test
    fun `빈 flux에 next(), block() 하면 null을 얻을 수 있다`() {
        val x = Flux.empty<String>().next().block()
    }

    @Test
    fun `Flux all은 하나라도 false 가 발견되면 즉시 중단할 것이다`() {
        val flux = Flux.just(
            { logger.info("1"); true },
            { logger.info("2"); false },
            { logger.info("3"); true },
            { logger.info("4"); true }
        )

        flux.all { it.invoke() }.block()
    }

    @Test
    fun `flatMap과 all으로 해도 한번씩만 실행한다`() {
        val preds = listOf(
            { logger.info("1"); true.toMono() },
            { logger.info("2"); false.toMono() },
            { logger.info("3"); true.toMono() },
            { logger.info("4"); true.toMono() }
        )

        preds.toFlux().flatMap { it.invoke() }.all { it }.block()
    }

    @Test
    fun concat() {
        val list = Flux.concat(Mono.just(1), Mono.empty(), Mono.just(3))
            .collectList()
            .block()

        assertThat(list).isEqualTo(listOf(1, 3))
    }

    @Test
    fun `concat은 publisher들을 하나하나 순차적으로 subscribe한다`() {
        val start = System.currentTimeMillis()
        val list = Flux.concat(
            Mono.just(1).delayElement(Duration.ofSeconds(1)),
            Mono.just(2).delayElement(Duration.ofSeconds(1)),
            Mono.just(3).delayElement(Duration.ofSeconds(1)),
            Mono.just(4).delayElement(Duration.ofSeconds(1)),
            Mono.just(5).delayElement(Duration.ofSeconds(1))
        )
            .collectList()
            .block()
        val end = System.currentTimeMillis()

        System.out.println(end - start)

        assertThat(list).isEqualTo(listOf(1, 2, 3, 4, 5))
    }

    @Test
    fun `mergeSequential은 publisher들을 동시에 subscribe 하지만 순서대로 배치한다`() {
        val start = System.currentTimeMillis()
        val list = Flux.mergeSequential(
            Mono.just(1).delayElement(Duration.ofSeconds(1)),
            Mono.just(2).delayElement(Duration.ofSeconds(1)),
            Mono.just(3).delayElement(Duration.ofSeconds(1)),
            Mono.just(4).delayElement(Duration.ofSeconds(1)),
            Mono.just(5).delayElement(Duration.ofSeconds(1))
        )
            .collectList()
            .block()
        val end = System.currentTimeMillis()

        System.out.println(end - start)

        assertThat(list).isEqualTo(listOf(1, 2, 3, 4, 5))
    }

    @Test
    fun `Flux test coverage`() {
        val xs = Flux.just(1)
        // 이 부분의 bytecode를 확인해보면...
        xs.any { 1 == it }.test().expectNext(true).verifyComplete()
    }

    @Test
    fun `first nonempty`() {
        Flux.mergeSequential(
            listOf(
                Mono.empty<Int>()
            ) + (1..10).map {
                Mono.just(it)
            }
        ).take(1).test().expectNext(1).verifyComplete()
    }

    @Test
    fun sequenceToFlux() {
        val flux = generateSequence { 1 }.toFlux()
        flux.take(2).test(2).expectNextCount(2).verifyComplete()
    }

    @Test
    fun sequence() {
        val seq = generateSequence { throw RuntimeException() }

        try {
            seq.iterator().next()
        } catch (e: RuntimeException) {

        }

        try {
            seq.iterator().next()
        } catch (e: RuntimeException) {
        }
    }

    @Test
    fun concatWithError() {
        val a = Flux.just(1).mergeWith(Flux.error(java.lang.RuntimeException())).mergeWith(Flux.just(2))
        val b = Flux.just(3, 4, 5)

        a.concatWith(b).onErrorContinue { _, _ -> }.collectList().block().equalsTo(listOf(1, 2, 3, 4, 5))
    }

    @Test
    fun FluxOfEmptyMono() {
        Flux.just(
            Mono.just(1),
            Mono.empty(),
            Mono.just(2)
        ).flatMap { it }.collectList().block().equalsTo(listOf(1, 2))
    }

    @Test
    fun take20() {
        println(Flux.just(1, 2, 3).take(Duration.ofDays(1)).take(1).collectList().block())
    }

}
