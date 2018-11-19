package com.npcode.learning.kotlin

import org.junit.Test

class LambdaTest {

    @Test
    fun test() {
        { it: String -> System.out.println(it) }.invoke("hello")
    }

    /*
    @Test
    fun infixTest() {
        "나" 는 (300 원 받았다)
        "나" 는 (50 원 줬다)
        "나" 는 (250 원 있어야한다)
    }
    */
}

enum class CurrencyCode {
    USD, KRW, JPY
}

data class Money(
    val amount: Long,
    val currencyCode: CurrencyCode
)

interface Action {
    fun run(subject: String)
}

//infix fun Int.`원`(action: Action): Action = action.run(Money(this, CurrencyCode.KRW))

infix fun String.`는`(action: Action) {
    action.run(this)
}
