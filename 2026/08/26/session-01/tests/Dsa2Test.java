import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class Dsa2Test {
    @Test void case01() {
        var tracker = new DsaProblem02.ActivityTracker();
        tracker.record("b"); tracker.record("a"); tracker.record("b"); tracker.record("c");
        assertEquals(List.of("b", "a", "c"), tracker.leaders(5));
    }

    @Test void case02() {
        var tracker = new DsaProblem02.ActivityTracker();
        tracker.record("z"); tracker.record("a"); tracker.record("m");
        assertEquals(List.of("a", "m"), tracker.leaders(2));
    }

    @Test void case03() {
        var tracker = new DsaProblem02.ActivityTracker();
        tracker.record("a"); tracker.record("a"); tracker.record("b");
        tracker.retract("a");
        assertEquals(List.of("a", "b"), tracker.leaders(2));
        tracker.retract("a");
        assertEquals(List.of("b"), tracker.leaders(2));
    }

    @Test void case04() {
        var tracker = new DsaProblem02.ActivityTracker();
        assertThrows(IllegalStateException.class, () -> tracker.retract("missing"));
        assertThrows(IllegalArgumentException.class, () -> tracker.record(" "));
        assertThrows(IllegalArgumentException.class, () -> tracker.leaders(0));
    }

    @Test void case05() {
        var tracker = new DsaProblem02.ActivityTracker();
        for (int i = 0; i < 100; i++) tracker.record("id-" + i);
        for (int i = 0; i < 50; i++) tracker.record("id-75");
        for (int i = 0; i < 20; i++) tracker.record("id-25");
        assertEquals(List.of("id-75", "id-25", "id-0"), tracker.leaders(3));
    }
}
