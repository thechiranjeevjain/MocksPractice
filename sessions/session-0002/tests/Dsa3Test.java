import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class Dsa3Test {
    private final List<DsaProblem03.Version> versions = List.of(
            new DsaProblem03.Version(10, "A"),
            new DsaProblem03.Version(20, "B"),
            new DsaProblem03.Version(40, "C"));

    @Test void beforeFirst() { assertTrue(DsaProblem03.valueAt(versions, 9).isEmpty()); }
    @Test void exact() { assertEquals("B", DsaProblem03.valueAt(versions, 20).orElseThrow()); }
    @Test void between() { assertEquals("B", DsaProblem03.valueAt(versions, 39).orElseThrow()); }
    @Test void afterLast() { assertEquals("C", DsaProblem03.valueAt(versions, 99).orElseThrow()); }
    @Test void empty() { assertTrue(DsaProblem03.valueAt(List.of(), 10).isEmpty()); }
    @Test void negativeTimestampsWork() {
        var history = List.of(new DsaProblem03.Version(-5, "old"), new DsaProblem03.Version(0, "new"));
        assertEquals("old", DsaProblem03.valueAt(history, -1).orElseThrow());
    }
}
