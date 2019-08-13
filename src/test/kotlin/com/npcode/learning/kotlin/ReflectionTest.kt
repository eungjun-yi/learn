package com.npcode.learning.kotlin

import com.npcode.learning.kotlin.EmptyFactory.Companion.dummy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

class ReflectionTest {

    object MyObject
    class MyClass {
        var foo = 123
        fun func() = "hello"
    }

    fun test() {
        var x = 0

        // unsupported
        // val prop = ::x

        val members = ReflectionTest::class.members

        // 둘 다 있잖아???
        MyObject::class.objectInstance
        MyClass::class.objectInstance

        val prop1 = MyClass::foo
        prop1.invoke(MyClass())
        val f1 = MyClass::func
        f1.invoke(MyClass())
    }

    data class Foo(val x: Int, val y: String, val z: SubFoo, val a: Long, val b: Double, val c: BigDecimal)

    data class SubFoo(val hello: String = "hi")

    @Test
    fun constructByReflection() {
        val foo: Foo = dummy()
        System.out.println(foo)
    }

}

class EmptyFactory {
    companion object {
        fun <T: Any> dummy(klass: KClass<T>): T  {
            return when {
                klass.isSubclassOf(BigDecimal::class) -> BigDecimal.ZERO
                klass.isSubclassOf(Number::class) -> 0
                klass.isSubclassOf(String::class) -> ""
                else -> {
                    val constructor = klass.constructors.sortedBy { it.parameters.size }.first()
                    val params = constructor.parameters
                    val args = params.map {
                        it to when(it.type.classifier) {
                            is KClass<*> -> dummy(it.type.classifier as KClass<*>)
                            else -> null
                        }
                    }.toMap()
                    constructor.callBy(args)
                }
            } as T
        }

        inline fun <reified T: Any> dummy(): T  {
            return dummy(T::class)
        }
    }
}

class CombinationFactory {
    companion object {
        fun combination(args: List<Pair<KParameter, Set<Any?>>>): List<List<Pair<KParameter, Any?>>> {
            if (args.isEmpty()) {
                return listOf(emptyList())
            }

            val tailCombinations = combination(args.drop(1))

            val headCombinations = args.first().second.map {
                args.first().first to it
            }

            return tailCombinations.flatMap { tail ->
                headCombinations.map { head ->
                    listOf(head) + tail
                }
            }
        }

        fun combination(args: Map<KParameter, Set<Any?>>): List<Map<KParameter, Any?>> {
            return combination(args.entries.map { it.toPair() }).map {
                it.toMap()
            }
        }

        fun <T: Any> combination(klass: KClass<T>, nullable: Boolean = false): Set<T?>  {
            val comb = when {
                klass.isSubclassOf(BigDecimal::class) -> bigDecimals
                klass.isSubclassOf(Number::class) -> numbers
                klass.isSubclassOf(CharSequence::class) -> charSequences
                klass.isSubclassOf(Boolean::class) -> booleans
                else -> {
                    val constructor = klass.constructors.minBy { it.parameters.size }!!
                    val params = constructor.parameters
                    val args = params.map {
                        val classifier = it.type.classifier
                        it to when (classifier) {
                            is KClass<*> -> combination(classifier as KClass<*>, it.type.isMarkedNullable)
                            else -> setOf(null)
                        }
                    }.toMap()
                    val combination = combination(args)
                    combination.map {
                        constructor.callBy(it)
                    }
                }
            } + if (nullable) listOf(null) else emptyList()

            return comb.map { it as T? }.toSet()
        }

        inline fun <reified T: Any> combination(): Set<T?>  {
            return combination(T::class)
        }

        private val bigDecimals = setOf(-BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE)
        private val numbers = setOf(-1, 0, 1)
        private val charSequences = setOf("")
        private val booleans = setOf(false, true)
    }
}

interface Generator<T> {
    fun anyBoolean(): Boolean
    fun anyInt(range: Iterable<Int>): Int
    fun generate(gen: Generator<T>.() -> T): T = gen.invoke(this)
}

data class GeneratorImpl2<T>(
    val boolValues: Iterator<Boolean>,
    val intValues: Iterator<Int>
): Generator<T> {
    override fun anyInt(range: Iterable<Int>) = intValues.next()
    override fun anyBoolean() = boolValues.next()
}

class ParameterCounter<T>: Generator<T> {

    var boolCount = 0
    var intRanges: MutableList<Iterable<Int>> = mutableListOf()

    override fun anyInt(range: Iterable<Int>): Int {
        intRanges.add(range)
        return 0
    }

    override fun anyBoolean(): Boolean {
        boolCount++
        return true
    }
}

fun booleanCombination(size: Int): List<List<Boolean>> = when(size) {
    0 -> listOf(emptyList())
    else -> booleanCombination(size - 1).map {
        listOf(listOf(true) + it, listOf(false) + it)
    }.flatten()
}

fun intCombination(ranges: List<Iterable<Int>>): List<List<Int>> = when(ranges.size) {
    0 -> listOf(emptyList())
    else -> intCombination(ranges.drop(1)).map { tail ->
        ranges.first().map { head ->
            listOf(head) + tail
        }
    }.flatten()
}

data class ParameterCombination(
    val boolValue: List<Boolean>,
    val intValue: List<Int>
)

fun parameterCombination(
    booleanSize: Int,
    intRanges: List<Iterable<Int>>
): List<ParameterCombination> {

    if (booleanSize == 0 && intRanges.isEmpty()) {
        return listOf(ParameterCombination(emptyList(), emptyList()))
    }

    return booleanCombination(booleanSize).map { bools ->
        intCombination(intRanges).map { ints ->
            ParameterCombination(bools, ints)
        }
    }.flatten()
}

fun <T> combination(gen: Generator<T>.() -> T): Set<T> {
    val parameterCounter = ParameterCounter<T>()
    parameterCounter.generate(gen)
    val parameterCombination = parameterCombination(
        parameterCounter.boolCount,
        parameterCounter.intRanges
    )
    return parameterCombination.map {
        GeneratorImpl2<T>(it.boolValue.iterator(), it.intValue.iterator()).generate(gen)
    }.toSet()
}
