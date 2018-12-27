import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

public class MonadTest {
    @Test
    public void test() {
        Function<Integer, Integer> f = x -> (x % 2 == 0) ? null : x;
        Function<Integer, String > g = y -> y == null ? "no value" : y.toString();

        Optional<Integer> opt = Optional.of(2);  // A value that f maps to null - this breaks .map

        System.out.println(opt.map(f).map(g));          // Optional.empty
        System.out.println(opt.map(f.andThen(g)));      // "no value"
    }
}
