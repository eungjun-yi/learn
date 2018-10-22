
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

    @Test
    fun repeat() {
        val mono = Mono.fromCallable { System.out.println("publisher works"); 1 }
        // map 할 때 마다 publisher가 실행된다.
        mono.map { it * 2 }.block()
        mono.map { it * 2 }.block()
    }

    @Test
    fun cache() {
        val mono = Mono.fromCallable { System.out.println("publisher works"); 1 }.cache()
        // cache 하면 한번만 실행된다.
        mono.map { it * 2 }.block()
        mono.map { it * 2 }.block()
    }
}
