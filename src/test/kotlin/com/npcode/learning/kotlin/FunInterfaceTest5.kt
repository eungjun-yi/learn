import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FunInterfaceTest5 {

    @Test
    fun test() {
        listOf(
            1,
            2,
        ).forEach { number ->
            val numberToPrint = number * 2
            val printNumber = PrintNumber { println(numberToPrint) }
            printNumber()
        }
    }
}

fun interface PrintNumber {
    operator fun invoke()
}
