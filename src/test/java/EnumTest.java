import org.junit.Test;

enum Value {
    A, B, C
}

public class EnumTest {

    @Test
    public void test() {
        System.out.println(Value.A.hashCode());
        System.out.println("A".hashCode());
    }

    @Test
    public void valueOf() {
        Value.valueOf("D");
    }
}
