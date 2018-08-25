import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

public class AtomicTest {

  long longFoo = 0;
  int intFoo = 0;

  Long objectLongFoo = 0L;
  Integer objectIntegerFoo = 0;

  @Test
  public void test() throws ExecutionException, InterruptedException {

    Runnable runnable = () -> {
      for (int i = 0; i < 10000000; i++) {
        longFoo++;
        intFoo++;
        objectLongFoo++;
        objectIntegerFoo++;

        longFoo++;
        intFoo++;
        objectLongFoo++;
        objectIntegerFoo++;
      }
    };

    CompletableFuture.allOf(
        CompletableFuture.runAsync(runnable),
        CompletableFuture.runAsync(runnable)
    ).get();

    System.out.println(longFoo);
    System.out.println(intFoo);
    System.out.println(objectLongFoo);
    System.out.println(objectIntegerFoo);
  }

  @Test
  public void test2() throws ExecutionException, InterruptedException {

    Runnable runnable = () -> {
      for (int i = 0; i < 100000000; i++) {
        longFoo = 1L;
        assertTrue(longFoo == 1L || longFoo == 10000000000L);
        longFoo = 10000000000L; // 백억
        assertTrue( longFoo == 1L || longFoo == 10000000000L);
      }
    };

    CompletableFuture.allOf(
        CompletableFuture.runAsync(runnable),
        CompletableFuture.runAsync(runnable)
    ).get();
  }
}
