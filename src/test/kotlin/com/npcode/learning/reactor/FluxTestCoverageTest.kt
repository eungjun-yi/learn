package com.npcode.learning.reactor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import reactor.core.publisher.Flux

class FluxTestCoverageTest {
    @Test
    fun `Flux test coverage`() {
        // 이 코드는 아무일도 하지 않는다???
        // java로 변환하면 predicate가 null로 보인다. 그래서 bytecode를 직접 읽을 수 밖에 없을듯
        val just = Flux.just(1)
        val any = just.any { 999 == it }
        assertThat(any.block()).isTrue()
    }
}
