
import org.junit.Test
import reactor.core.publisher.Mono

class MonoTest {

    @Test(expected = java.lang.RuntimeException::class)
    fun test() {
        val mono: Mono<String> = Mono.just("y").flatMap {
            if (it == "x") Mono.just("X") else Mono.error(RuntimeException())
        }
        mono.block()
    }
}
