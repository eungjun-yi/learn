package com.npcode.learning.kotlin

import org.junit.jupiter.api.Test
import java.nio.file.Paths

class PathsTest {

    @Test
    fun test() {
        val path = Paths.get("a/b/c")
        path.parent.toFile().mkdirs()
    }
}
