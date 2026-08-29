import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Dsa1Test {
    @Test void case01() { assertEquals(0, DsaProblem01.longestDistinctSpan("")); }
    @Test void case02() { assertEquals(5, DsaProblem01.longestDistinctSpan("abcaef")); }
    @Test void case03() { assertEquals(1, DsaProblem01.longestDistinctSpan("zzzz")); }
    @Test void case04() { assertEquals(3, DsaProblem01.longestDistinctSpan("dvdf")); }
    @Test void case05() { assertEquals(2, DsaProblem01.longestDistinctSpan("abba")); }
    @Test void case06() { assertEquals(8, DsaProblem01.longestDistinctSpan("abcdefgh")); }
    @Test void case07() { assertEquals(4, DsaProblem01.longestDistinctSpan("a\u0000bc\u0000d")); }
}
