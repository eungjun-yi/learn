import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FunInterfaceTest4 {

    @Test
    fun test() {
        multiply(1, 2) shouldBe 2
        SimpleMultiply(1, 2) shouldBe 2
        RepeatSum()(1, 2) shouldBe 2
        val bigNumberMultiply: Multiply = BigNumberMultiply()
        bigNumberMultiply(1, 2) shouldBe 2
    }
}

// 네이밍을 어떻게 하는게 최선일지
// 구현의 이름을 구체적으로 정할 수 없다면 인터페이스쪽의 이름을 더 추상적인 것으로 택하는 건 어떨까?

fun interface Multiply {
    operator fun invoke(x: Int, y: Int): Number
}

val multiply = Multiply { x, y -> x * y }

object SimpleMultiply: Multiply {
    override fun invoke(x: Int, y: Int) = x * y
}

class RepeatSum: Multiply {
    override fun invoke(x: Int, y: Int) = (1..y).sumBy { x }
}

class BigNumberMultiply: Multiply {
    override fun invoke(x: Int, y: Int) = x.toBigDecimal().multiply(y.toBigDecimal())
}
