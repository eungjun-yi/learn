package com.npcode.learning.kotlin.coverage

inline fun inline1(yes: Boolean) {
    if (yes)
        println("yes")
    else
        println("no")
}

inline fun inline2() {
    println("inlined")
}

fun foo1() {
    inline1(true)
    inline2()
}

fun foo2() {
    inline1(false)
    inline2()
}
