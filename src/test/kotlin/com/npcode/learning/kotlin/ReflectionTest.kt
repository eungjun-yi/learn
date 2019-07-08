package com.npcode.learning.kotlin

import com.npcode.learning.kotlin.EmptyFactory.Companion.dummy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.reflect.KClass
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
