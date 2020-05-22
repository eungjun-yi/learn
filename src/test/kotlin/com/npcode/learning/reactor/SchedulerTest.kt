package com.npcode.learning.reactor

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

class SchedulerTest {

    @Test
    fun test() {
        Schedulers.single().schedule {
            println("${Thread.currentThread()} a")
        }
        println("${Thread.currentThread()} 1")
        Schedulers.single().schedule {
            println("${Thread.currentThread()} b")
        }
        println("${Thread.currentThread()} 2")

        Mono.just(1).cache()
    }
}
