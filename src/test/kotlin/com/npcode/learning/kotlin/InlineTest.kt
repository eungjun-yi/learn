package com.npcode.learning.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InlineTest {
    @Test
    fun reflectionAndInline() {

        class A {
            val name = genName({}).javaClass.name
        }

        class B {
            val name = genName({}).javaClass.name
        }

        assertThat(A().name).isEqualTo("com.npcode.learning.kotlin.InlineTest\$reflectionAndInline\$A\$name\$1")
        assertThat(B().name).isEqualTo("com.npcode.learning.kotlin.InlineTest\$reflectionAndInline\$B\$name\$1")
    }
}

fun genName(f: () -> Unit): () -> Unit {
    f.invoke()
    return f
}
