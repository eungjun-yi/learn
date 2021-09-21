import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FailSafeByGenericsTest {
    @Test
    public void test() {
        GetGreeting getGreeting = new GetGreeting();

        assertThat(getGreeting.invoke()).isEqualTo("hello");
        assertThat(getGreeting.failSafe()).isEqualTo("hello");
    }
}

class GetGreeting implements FailSafe3<String> {
    public String invoke() {
        return "hello";
    }
}

