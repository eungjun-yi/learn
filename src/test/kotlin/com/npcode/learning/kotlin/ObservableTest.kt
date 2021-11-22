package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test
import kotlin.properties.Delegates

class ObservableTest {
    @Test
    fun test() {
        var foo: Int by Delegates.observable(0) { _, old, new ->
            println("foo changed: $old -> $new")
        }

        foo = 1
        foo = 2
    }
}
