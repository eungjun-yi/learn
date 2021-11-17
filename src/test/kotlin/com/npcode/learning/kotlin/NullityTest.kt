package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test
import java.io.InputStream
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

class NullityTest() {

    @Test
    fun test() {
        val x: Int by Delegates.notNull() // 이게 어떻게 가능할까? kotlin에서 직접 지원해서인가?
        println(x) // throw IllegalStateException
    }
}

class WritableFieldHolder(
    var a: Int = 0
)
