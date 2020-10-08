package com.npcode.learning.testing

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class KotestLocalDateTimeTest: FreeSpec({
    "LocalDateTime 비교" {
        val now = LocalDateTime.now()
        now shouldBe now
    }
})
