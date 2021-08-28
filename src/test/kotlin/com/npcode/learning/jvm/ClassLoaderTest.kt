package com.npcode.learning.jvm

import org.junit.jupiter.api.Test
import java.util.concurrent.ForkJoinPool
import kotlin.reflect.jvm.jvmName

class ClassLoaderTest {

    @Test
    fun test() {
        Class.forName(name)

        ForkJoinPool(10).submit {
            listOf(1, 2, 3).parallelStream().forEach {
                println(Class.forName(name))
            }
        }.get()
    }
}

class Foo1()

val name = Foo1::class.jvmName
