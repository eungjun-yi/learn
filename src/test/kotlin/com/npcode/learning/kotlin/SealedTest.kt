package com.npcode.learning.kotlin

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

fun <T: Any> KClass<T>.sealedFinalSubclasses(): List<KClass<out T>> = this.sealedSubclasses.flatMap {
    if (it.isFinal) listOf(it) else it.sealedFinalSubclasses()
}

class SealedTest {

    @Test
    fun test() {
        println(SuperSealed::class.sealedSubclasses)
        SuperSealed.valueOf("1") shouldBe Leaf1
        SuperSealed.valueOf("2") shouldBe Leaf2
        SuperSealed.valueOf("3") shouldBe Leaf3
    }
}

sealed class SuperSealed {
    abstract val value: String

    companion object {
        fun valueOf(value: String): SuperSealed =
            SuperSealed::class.sealedFinalSubclasses().mapNotNull { it.objectInstance }.first { it.value == value }
    }
}

sealed class SubSealed1: SuperSealed()

object Leaf1: SuperSealed() {
    override val value: String = "1"
}

object Leaf2: SubSealed1() {
    override val value: String = "2"
}

object Leaf3: SubSealed1() {
    override val value: String = "3"
}
