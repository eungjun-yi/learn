import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class LazyDataClassTest {
    @Test
    fun test() {
        Bar(GetNumber(1)) shouldNotBe Bar(GetNumber(2))
        Bar(GetNumber(1)) shouldBe Bar(GetNumber(1))
    }

    @Test
    fun test2() {
        sortedSetOf(1, 2, 3, 3) shouldBe sortedSetOf(3, 3, 1, 2)
        sortedSetOf(1, 2, 3, 3) shouldNotBe sortedSetOf(3, 1, 2)
    }
}

data class GetNumber(
    val number: Int
) {
    operator fun invoke() = number
}

data class Bar(
    private val getX: GetNumber
) {
    val x: Int by lazy {
        getX()
    }
}
