import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FunInterfaceTest3 {

}

fun interface Foo1 {
    operator fun invoke(x: Int, y: Int)
}

fun interface Foo2 {
    operator fun invoke(x: Int)
}

// 1. fun interface는 파라메터가 디폴트값을 가질 수 없다.
// 2. fun interface는 2개 이상의 메서드를 가질 수 없다.
// 3. overriding function은 디폴트값을 가질 수 없다.
// 따라서 이 정도가 최선이다.
// 아니면 parameter object를 만드는 방법도 있다. 호출 코드는 좀 길어지겠지만.
class Foo12: Foo1, Foo2 {
    override operator fun invoke(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override operator fun invoke(x: Int) = invoke(x, 1)
}
