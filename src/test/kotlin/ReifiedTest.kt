class ReifiedTest {

    class Foo {
        fun bar1() = "bar1"

        companion object {
            fun bar2() = "bar2"
        }
    }

    inline fun <T : Foo> foo1(t: T) {
        Foo.bar2()
        // T::class.java // compile error
        t.bar1()
        // T.bar2() // compile error
    }

    inline fun <reified T : Foo> foo2(t: T) {
        Foo.bar2()
        T::class.java
        t.bar1()
        // T.bar2() // compile error
    }
}
