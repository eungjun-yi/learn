import org.junit.Test
import kotlin.reflect.KProperty

class DelegateTest {
    @Test
    fun test() {
        val foo = Foo()
        println(foo.bar)
        println(foo.bar)
    }
}

class Foo {
    val bar by run {
        println("new delegator")
        MyProperty()
    }

}

class MyProperty {
    operator fun getValue(foo: Foo, property: KProperty<*>): Any {
        return 1
    }
}
