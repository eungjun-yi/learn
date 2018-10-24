package com.npcode.learning.kotlin

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
}
