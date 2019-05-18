public class ExceptionTest {

    public void test() {
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            // 아무거나
        }
    }

}
