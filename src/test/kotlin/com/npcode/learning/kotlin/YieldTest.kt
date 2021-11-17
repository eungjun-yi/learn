package com.npcode.learning.kotlin

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class YieldTest {

    @Test
    fun test() {
        primes.take(5).toList() shouldBe listOf(2, 3, 5, 7, 11)
    }

    @Test
    fun test2() {
        println(primes2.take(5).toList())
    }

    @Test
    fun test3() {
        var last = 1
        val seq = sequence {
            yield(last * 2)
        }

        println(seq.take(10).toList())
    }
}

val primes: Sequence<Int> = sequence {
    var numbers = generateSequence(2) { it + 1 }

    while (true) {
        val prime = numbers.first()
        yield(prime)
        numbers = numbers.filterNot { it % prime == 0 }
    }
}

val primes2: Sequence<Int> = sequence {
    var numbers = generateSequence(2) { it + 1 }

    var prime: Int
    while (true) {
        prime = numbers.first()
        yield(prime)
        numbers = numbers.drop(1).filterNot {
            it % prime == 0
        }
    }
}
