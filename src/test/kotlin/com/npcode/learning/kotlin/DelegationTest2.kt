package com.npcode.learning.kotlin

class DelegationTest2 {
    data class DefaultCommonPart(
        override val a: String,
        override val b: String
    ): CommonPart

    interface CommonPart {
        val a: String
        val b: String
    }

    data class MyDataClass(
        val common: DefaultCommonPart, // 이것을 숨기기 어렵다. private으로 하면 copy가 안됨
        val c: String,
    ): CommonPart by common

    private fun DefaultCommonPart.emptyA() = copy(a = "")

    fun test() {
        val data1 = MyDataClass(
            common = DefaultCommonPart(a = "a", b = "b"),
            c = "c"
        )

        val data2 = data1.run { copy(common = common.emptyA()) }
    }
}
