import org.junit.Test
import io.kotest.property.*
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string

class ArbTest {

    @Test
    fun test() {
        Arb.string().next(predicate = { it.length > 2 })
    }
}
