public class TwoSignOneImplTest {
}

interface X {

}

interface Y {

}

class Z implements X, Y {

}

interface A<T extends X> {
    public void foo(T item);
}

class C implements A<Z> {

    @Override
    public void foo(Z item) {

    }
}
