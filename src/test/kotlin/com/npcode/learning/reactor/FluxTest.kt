package com.npcode.learning.reactor
import org.junit.Test
import reactor.core.Disposables
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import reactor.test.test

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
                    System.out.println(it.context.get("foo") as Int)
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
                    System.out.println("${it.type} foo: ${it.context.get("foo") as Int}")
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
            .onErrorContinue { t, u ->

            }
            .collectList()
            .test()
            .expectNext(listOf(2, 1))
            .verifyComplete()
    }
}
