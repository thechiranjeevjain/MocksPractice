import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class Dsa1Test {
    @Test void empty() { assertArrayEquals(new int[]{-1, -1}, DsaProblem01.searchRange(new int[]{}, 4)); }
    @Test void absent() { assertArrayEquals(new int[]{-1, -1}, DsaProblem01.searchRange(new int[]{1, 3, 5}, 4)); }
    @Test void one() { assertArrayEquals(new int[]{0, 0}, DsaProblem01.searchRange(new int[]{7}, 7)); }
    @Test void duplicates() { assertArrayEquals(new int[]{1, 3}, DsaProblem01.searchRange(new int[]{1, 2, 2, 2, 4}, 2)); }
    @Test void all() { assertArrayEquals(new int[]{0, 3}, DsaProblem01.searchRange(new int[]{5, 5, 5, 5}, 5)); }
    @Test void ends() { assertArrayEquals(new int[]{0, 1}, DsaProblem01.searchRange(new int[]{2, 2, 3, 4, 4}, 2)); }
}
