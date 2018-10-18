package com.fasterxml.jackson.databind

import org.junit.Test

class JacksonTest {

    @Test
    fun test() {
        val items: List<String> = ObjectMapper().reader().readValue("[\"테스트1\",\"테스트2\"]")
    }
}
