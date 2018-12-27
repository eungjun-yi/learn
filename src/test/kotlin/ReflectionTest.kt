import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class ReflectionTest {
    @Test
    fun test() {
        data class Foo(
            val x: Int,
            val y: Int
        )

        val prop1: KProperty1<Foo, Int> = Foo::x
        val prop2: KProperty1<Foo, Int> = Foo::y
        // type이 * 일 수 밖에 없다는 단점이 있다.
        val props2: Collection<KProperty1<Foo, *>> = Foo::class.memberProperties

        // 통과는 함
        assertThat(listOf(prop1, prop2)).isEqualTo(props2)
    }
}