import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Dsa2Test {
    @Test void canonical() { assertEquals(15, DsaProblem02.minimumCapacity(new int[]{1,2,3,4,5,6,7,8,9,10}, 5)); }
    @Test void threeDays() { assertEquals(6, DsaProblem02.minimumCapacity(new int[]{3,2,2,4,1,4}, 3)); }
    @Test void manyDays() { assertEquals(3, DsaProblem02.minimumCapacity(new int[]{1,2,3,1,1}, 4)); }
    @Test void oneDay() { assertEquals(10, DsaProblem02.minimumCapacity(new int[]{2,3,5}, 1)); }
    @Test void onePerDay() { assertEquals(5, DsaProblem02.minimumCapacity(new int[]{2,3,5}, 3)); }
    @Test void singleton() { assertEquals(9, DsaProblem02.minimumCapacity(new int[]{9}, 1)); }
}
