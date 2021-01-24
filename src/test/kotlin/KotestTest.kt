import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec

class KotestTest: FreeSpec({
    "test1" {
        println("ok1")
    }

    "test2" {
        println("ok2")
    }
})

class KotestTest2: FunSpec({
    test("test1") {
        println("ok1")
    }

    test("test2") {
        println("ok2")
    }
})

class KotestTest3: StringSpec({
    "test1" {
        println("ok1")
    }

    "test2" {
        println("ok2")
    }
})
