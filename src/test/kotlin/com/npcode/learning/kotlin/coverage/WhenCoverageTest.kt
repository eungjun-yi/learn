package com.npcode.learning.kotlin.coverage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class WhenCoverageTest {

    @Test
    fun test() {
        doSomethingWithWhen("a")
        doSomethingWithWhen("b")
        doSomethingWithWhen("c")
    }
}
