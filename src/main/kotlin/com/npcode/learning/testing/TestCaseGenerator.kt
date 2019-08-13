package com.npcode.learning.testing

class TestCaseGenerator<T> {

    fun generateReversedPairs(
        pair: Pair<T, Boolean>,
        vararg propertyUpdaters: BooleanPropertyUpdater<T>
    ): Set<Pair<T, Boolean>> {
        val expected = pair.second
        val given = pair.first
        val all: Set<T> = generateAll(given, propertyUpdaters.toList())
        return all.map {
            it to !expected
        }.filterNot {
            it.first == given
        }.toSet()
    }
}

data class BooleanPropertyUpdater<T>(
    val update: (T, Boolean) -> T
)

fun <T> trueAnyOf(base: T, propertyUpdaters: List<BooleanPropertyUpdater<T>>): Set<T> {
    val all = generateAll(base, propertyUpdaters)
    val except = falseAllOf(base, propertyUpdaters)

    return all - except
}

fun <T> trueAllOf(base: T, propertyUpdaters: List<BooleanPropertyUpdater<T>>): T =
    propertyUpdaters.fold(base) { r, t ->
        t.update(r, true)
    }

fun <T> falseAnyOf(base: T, propertyUpdaters: List<BooleanPropertyUpdater<T>>): Set<T> {
    return generateAll(base, propertyUpdaters) - trueAllOf(base, propertyUpdaters)
}

fun <T> falseAllOf(base: T, propertyUpdaters: List<BooleanPropertyUpdater<T>>): T =
    propertyUpdaters.fold(base) { r, t ->
        t.update(r, false)
    }

private fun <T> generateAll(
    base: T,
    propertyUpdaters: List<BooleanPropertyUpdater<T>>
) = generateAll(base, listOf(true, false), propertyUpdaters)

private fun <T> generateAll(
    base: T,
    values: List<Boolean>,
    propertyUpdaters: List<BooleanPropertyUpdater<T>>
): Set<T> {
    val head = propertyUpdaters.take(1)
    val tail = propertyUpdaters.drop(1)
    val heads = head.flatMap { updater ->
        values.map {
            updater.update(base, it)
        }
    }.toSet()

    val tails = heads.flatMap {
        generateAll(it, tail)
    }

    return heads + tails
}
