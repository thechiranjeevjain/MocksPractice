import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class Dsa3Test {
    @Test void case01() {
        var tasks = List.of("quote", "risk", "route", "audit");
        var dependencies = List.of(
                new DsaProblem03.Dependency("quote", "risk"),
                new DsaProblem03.Dependency("risk", "route"));
        assertEquals(List.of("audit", "quote", "risk", "route"), DsaProblem03.buildPlan(tasks, dependencies));
    }

    @Test void case02() {
        var tasks = List.of("a", "b", "c");
        var dependencies = List.of(
                new DsaProblem03.Dependency("a", "b"),
                new DsaProblem03.Dependency("a", "b"),
                new DsaProblem03.Dependency("b", "c"));
        assertEquals(List.of("a", "b", "c"), DsaProblem03.buildPlan(tasks, dependencies));
    }

    @Test void case03() {
        var tasks = List.of("a", "b");
        var dependencies = List.of(
                new DsaProblem03.Dependency("a", "b"),
                new DsaProblem03.Dependency("b", "a"));
        assertEquals(List.of(), DsaProblem03.buildPlan(tasks, dependencies));
    }

    @Test void case04() {
        assertEquals(List.of("a", "b", "c"), DsaProblem03.buildPlan(List.of("c", "a", "b"), List.of()));
    }

    @Test void case05() {
        var tasks = List.of("a", "b", "c", "d", "e");
        var dependencies = List.of(
                new DsaProblem03.Dependency("a", "d"),
                new DsaProblem03.Dependency("b", "d"),
                new DsaProblem03.Dependency("d", "e"));
        assertEquals(List.of("a", "b", "c", "d", "e"), DsaProblem03.buildPlan(tasks, dependencies));
    }
}
