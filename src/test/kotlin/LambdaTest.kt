import org.junit.Test

class LambdaTest {

    @Test
    fun test() {
        { it: String -> System.out.println(it) }.invoke("hello")
    }
}