import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FunInterfaceTest6 {

    @Test
    fun test() {
        val fun1: Fun1 = FunImpl()
        fun1(1) shouldBe 2
        (fun1 as Fun1WithDefaultValues)() shouldBe 2
    }
}

fun interface Fun1 {
    operator fun invoke(x: Int): Int
}

interface Fun1WithDefaultValues {
    operator fun invoke(x: Int = 1): Int
}

class FunImpl : Fun1, Fun1WithDefaultValues {
    override fun invoke(x: Int): Int = x * 2
}
