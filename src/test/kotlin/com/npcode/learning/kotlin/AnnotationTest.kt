package com.npcode.learning.kotlin

@Target(AnnotationTarget.CLASS)
annotation class MyAnnTest(val name: String)

@MyAnnTest("test")
class MyTest {
}