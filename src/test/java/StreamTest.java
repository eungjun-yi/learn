import org.junit.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class StreamTest {

  class MyValue {
    private final String date;
    private final int round;
    private final int value;

    public MyValue(String date, int round, int value) {
      this.date = date;
      this.round = round;
      this.value = value;
    }

    public String getDate() {
      return date;
    }

    public int getRound() {
      return round;
    }

    public int getValue() {
      return value;
    }
  }

  @Test
  public void test() {
    // 매일 첫 번째 값만 남긴다.
    List<MyValue> given = asList(
        new MyValue("2018-01-01", 1, 100),
        new MyValue("2018-01-01", 1, 109),
        new MyValue("2018-01-01", 2, 101),
        new MyValue("2018-01-02", 1, 102),
        new MyValue("2018-01-02", 2, 100),
        new MyValue("2018-01-03", 2, 101)
    );

    Map<String, List<MyValue>> actual = given
        .stream()
        .collect(Collectors.groupingBy(MyValue::getDate))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Entry::getKey,
                entry -> {
                  List<MyValue> ls = entry.getValue();
                  Optional<Integer> minRound = ls.stream()
                      .map(MyValue::getRound)
                      .min(Integer::compareTo);
                  if (minRound.isPresent()) {
                    return ls.stream()
                        .filter(it -> minRound.get().equals(it.getRound()))
                        .collect(Collectors.toList());
                  } else {
                    return ls;
                  }
                }
            )
        );

    // Then
    Map<String, List<MyValue>> expected  = new HashMap<>();
    expected.put("2018-01-01", asList(
        new MyValue("2018-01-01", 1, 100),
        new MyValue("2018-01-01", 1, 109)));
    expected.put("2018-01-02", asList(
        new MyValue("2018-01-02", 1, 102)));
    expected.put("2018-01-03", asList(
        new MyValue("2018-01-03", 2, 101)));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void handleDuplication() {
    // 매일 첫 라운드만 남긴다.
    List<MyValue> given = asList(
        new MyValue("2018-01-01", 1, 100),
        new MyValue("2018-01-01", 1, 109),
        new MyValue("2018-01-01", 2, 101),
        new MyValue("2018-01-02", 2, 100),
        new MyValue("2018-01-02", 1, 102),
        new MyValue("2018-01-03", 2, 101)
    );

    Map<String, MyValue> actual = given
        .stream()
        .collect(
            Collectors.toMap(
                MyValue::getDate,
                it -> it,
                BinaryOperator.minBy(Comparator.comparing(MyValue::getRound))
            )
        );

    // Then
    Map<String, MyValue> expected  = new HashMap<>();
    expected.put("2018-01-01", new MyValue("2018-01-01", 1, 100));
    expected.put("2018-01-02", new MyValue("2018-01-02", 1, 102));
    expected.put("2018-01-03", new MyValue("2018-01-03", 2, 101));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void mapOfMap() {

    class Key {
      private final String date;
      private final int round;

      public Key(String date, int round) {
        this.date = date;
        this.round = round;
      }

      public String getDate() {
        return date;
      }

      public int getRound() {
        return round;
      }
    }

    Map<Key, Integer> given = new HashMap<>();
    given.put(new Key("2018-01-01", 1), 100);
    given.put(new Key("2018-01-01", 2), 101);
    given.put(new Key("2018-01-01", 3), 102);
    given.put(new Key("2018-01-02", 2), 100);
    given.put(new Key("2018-01-02", 1), 102);
    given.put(new Key("2018-01-03", 2), 101);

    // When
    Map<String, Map<Integer, Integer>> actual = given.entrySet().stream()
        .collect(Collectors.groupingBy(
            it -> it.getKey().getDate(),
            Collectors.toMap(
                it -> it.getKey().getRound(),
                Entry::getValue
            )
        ));

    // Then
    Map<String, Map<Integer, Integer>> expected  = new HashMap<>();
    Map<Integer, Integer> day1  = new HashMap<>();
    day1.put(1, 100);
    day1.put(2, 101);
    day1.put(3, 102);
    Map<Integer, Integer> day2  = new HashMap<>();
    day2.put(1, 102);
    day2.put(2, 100);
    Map<Integer, Integer> day3  = new HashMap<>();
    day3.put(2, 101);
    expected.put("2018-01-01", day1);
    expected.put("2018-01-02", day2);
    expected.put("2018-01-03", day3);
    assertThat(actual).isEqualTo(expected);
  }
}
