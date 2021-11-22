package com.npcode.learning.kotlin.coverage

fun func1(): String = "yes"

// 커버리지 100% 불가
fun doSomethingWithElvis(a: Int?): String =
    a?.let {
        func1() // 이 함수의 리턴 타입이 primitive가 아니기 때문에 null 검사를 한다.
    } ?: "no"
