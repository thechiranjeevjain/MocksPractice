import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class DebuggingTest {
    @Test void usesValueEqualityAndAggregatesRequestedDesk() {
        String desk = new String("FX");
        var fills = List.of(
                new DebuggingRound.Fill("o1", new String("FX"), 2, 100),
                new DebuggingRound.Fill("o2", "EQ", 9, 100),
                new DebuggingRound.Fill("o3", "FX", 3, 100));
        assertEquals(List.of(new DebuggingRound.DeskTotal(desk, 500)), DebuggingRound.summarize(fills, desk));
    }

    @Test void keepsLongArithmetic() {
        var fills = List.of(new DebuggingRound.Fill("o1", "FX", 2_000_000, 2_000_000));
        assertEquals(4_000_000_000L, DebuggingRound.summarize(fills, "FX").getFirst().notional());
    }

    @Test void duplicateOrderIdsAreLegitimateFills() {
        var fills = List.of(
                new DebuggingRound.Fill("same", "FX", 1, 10),
                new DebuggingRound.Fill("same", "FX", 2, 10));
        assertEquals(List.of(new DebuggingRound.DeskTotal("FX", 30)), DebuggingRound.summarize(fills, "FX"));
    }

    @Test void noMatchReturnsEmpty() {
        assertEquals(List.of(), DebuggingRound.summarize(
                List.of(new DebuggingRound.Fill("o1", "EQ", 1, 10)), "FX"));
    }

    @Test void validatesBoundaries() {
        assertThrows(IllegalArgumentException.class, () -> DebuggingRound.summarize(null, "FX"));
        assertThrows(IllegalArgumentException.class, () -> DebuggingRound.summarize(List.of(), " "));
    }
}
