import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DebuggingTest {
    @Test void case01() {
        var input = List.of(
                new DebuggingRound.Execution("o1", "X", 4),
                new DebuggingRound.Execution("o1", "Y", 9),
                new DebuggingRound.Execution("o1", "X", 3));
        assertEquals(List.of(
                new DebuggingRound.Aggregate("o1", "Y", 9),
                new DebuggingRound.Aggregate("o1", "X", 7)), DebuggingRound.aggregate(input));
    }

    @Test void case02() {
        var input = List.of(
                new DebuggingRound.Execution("b", "X", 5),
                new DebuggingRound.Execution("a", "Y", 5),
                new DebuggingRound.Execution("a", "X", 5));
        assertEquals(List.of(
                new DebuggingRound.Aggregate("a", "X", 5),
                new DebuggingRound.Aggregate("a", "Y", 5),
                new DebuggingRound.Aggregate("b", "X", 5)), DebuggingRound.aggregate(input));
    }

    @Test void case03() {
        var input = List.of(
                new DebuggingRound.Execution("o", "X", Integer.MAX_VALUE),
                new DebuggingRound.Execution("o", "X", Integer.MAX_VALUE));
        assertEquals(2L * Integer.MAX_VALUE,
                DebuggingRound.aggregate(input).getFirst().quantity());
    }

    @Test void case04() {
        assertThrows(IllegalArgumentException.class, () -> DebuggingRound.aggregate(null));
        assertThrows(IllegalArgumentException.class, () ->
                DebuggingRound.aggregate(java.util.Arrays.asList(new DebuggingRound.Execution("o", "X", 1), null)));
        assertThrows(IllegalArgumentException.class, () ->
                DebuggingRound.aggregate(List.of(new DebuggingRound.Execution(" ", "X", 1))));
    }

    @Test void case05() {
        var input = new ArrayList<>(List.of(new DebuggingRound.Execution("o", "X", 1)));
        var before = List.copyOf(input);
        DebuggingRound.aggregate(input);
        assertEquals(before, input);
    }
}
