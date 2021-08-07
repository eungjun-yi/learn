package com.npcode.learning.coroutine

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun hello() = runBlocking {
    launch {
        println("hello")
    }
}
