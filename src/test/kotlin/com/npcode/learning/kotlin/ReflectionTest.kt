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
    fun <U : Any> anyEnum(enumClass: KClass<U>): U
}

inline fun <T, reified U: Any> Generator<T>.anyEnum(): U {
    return this.anyEnum(U::class)
}

data class GeneratorImpl2<T>(
    val boolValues: Iterator<Boolean>,
    val intValues: Iterator<Int>,
    val enumValues: Map<KClass<out Any>, Iterator<Any>>
): Generator<T> {
    override fun <U : Any> anyEnum(enumClass: KClass<U>): U = (enumValues[enumClass] as Iterator<U>).next()
    override fun anyInt(range: Iterable<Int>) = intValues.next()
    override fun anyBoolean() = boolValues.next()
}

class ParameterCounter<T>: Generator<T> {

    var boolCount = 0
    var intRanges: MutableList<Iterable<Int>> = mutableListOf()
    var enumCount: MutableMap<KClass<out Any>, Int> = mutableMapOf()

    override fun <U : Any> anyEnum(enumClass: KClass<U>): U {
        enumCount[enumClass] = (enumCount[enumClass] ?: 0) + 1
        return enumClass.java.enumConstants.first()
    }

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

fun enumCombination(
    type: KClass<out Any>,
    count: Int
): List<List<Any>> {
    return when(count) {
        0 -> listOf(emptyList())
        else -> {
            val heads = type.java.enumConstants
            enumCombination(type, count - 1).map { tail ->
                heads.map { head ->
                    listOf(head) + tail
                }
            }.flatten()
        }
    }
}

fun enumCombination(
    countByEnumType: Map<KClass<out Any>, Int>
): List<Map<KClass<out Any>, List<Any>>> {
    return when(countByEnumType.size) {
        0 -> listOf(emptyMap())
        else -> {
            val head = countByEnumType.entries.first()
            val headType = head.key
            val headValues = enumCombination(headType, head.value)

            val tail = countByEnumType.entries.drop(1).map {
                it.key to it.value
            }.toMap()

            val enumCombination = enumCombination(tail)
            headValues.map { headValue ->
                enumCombination.map { tailValues ->
                    val result = mutableMapOf(
                        headType to headValue
                    )
                    result.putAll(tailValues)
                    result.toMap()
                }
            }.flatten()
        }
    }
}

data class ParameterCombination(
    val boolValue: List<Boolean>,
    val intValue: List<Int>,
    val enumValues: Map<KClass<out Any>, List<Any>>
)

fun parameterCombination(
    booleanSize: Int,
    intRanges: List<Iterable<Int>>,
    enumCount: MutableMap<KClass<out Any>, Int>
): List<ParameterCombination> {

    if (booleanSize == 0 && intRanges.isEmpty() && enumCount.isEmpty()) {
        return listOf(
            ParameterCombination(
                emptyList(),
                emptyList(),
                emptyMap()
            )
        )
    }

    return booleanCombination(booleanSize).map { bools ->
        intCombination(intRanges).map { ints ->
            enumCombination(enumCount).map { enums ->
                ParameterCombination(bools, ints, enums)
            }
        }.flatten()
    }.flatten()
}

fun <T> combination(gen: Generator<T>.() -> T): Set<T> {
    val parameterCounter = ParameterCounter<T>()
    parameterCounter.generate(gen)
    val parameterCombination = parameterCombination(
        parameterCounter.boolCount,
        parameterCounter.intRanges,
        parameterCounter.enumCount
    )
    return parameterCombination.map {
        GeneratorImpl2<T>(
            it.boolValue.iterator(),
            it.intValue.iterator(),
            it.enumValues.map {
                it.key to it.value.iterator()
            }.toMap()
        ).generate(gen)
    }.toSet()
}
