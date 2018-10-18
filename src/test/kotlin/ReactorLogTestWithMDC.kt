
import mu.KotlinLogging
import org.junit.Test
import org.slf4j.MDC
import reactor.core.publisher.Signal
import reactor.core.publisher.toFlux
import java.time.Duration
import java.util.function.Consumer

class ReactorLogTestWithMDC {

    @Test
    fun log() {
        listOf(1, 2, 3).toFlux()
                .doOnEach(mdcUpdater())
                .doOnNext { logger.info("Print hello message") }
                .map { System.out.println("Hello, $it") }
                // 아래 줄은 반드시 doOnEach 보다 나중이어야한다.
                .subscriberContext {
                    it.put("userNo", 123).put("foo", "bar2")
                }
                .log()
                // 멀티스레드로 동작하게 하려고
                .delayElements(Duration.ofMillis(1))
                .blockLast()
    }

    companion object {
        private val logger = KotlinLogging.logger {}

        fun <T> mdcUpdater(): Consumer<Signal<T>> {
            return Consumer { signal ->
                // onNext시에만 동작하려면 애초에 doOnNext()에서 호출해도 되었을 것 같지만,
                // 어째선지 doOnNext()에서는 context를 얻을 수 없다.
                if (signal.isOnNext) {
                    val userNo: Int? = signal.context.get("userNo")
                    userNo?.let {
                        logger.info { "update mdc" }
                        MDC.put("userNo", it.toString())
                    }
                }
            }
        }
    }
}