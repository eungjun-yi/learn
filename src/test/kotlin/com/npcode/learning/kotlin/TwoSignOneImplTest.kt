package com.npcode.learning.kotlin

class TwoSignOneImplTest {
}

interface X

interface Y

class Z : X, Y

interface A<T : X> {
    fun foo(item: T)
}

interface B<T : Y> {
    fun foo(item: T)
}

class C : A<Z>, B<Z> {
    override fun foo(item: Z) {
        TODO("Not yet implemented")
    }
}


