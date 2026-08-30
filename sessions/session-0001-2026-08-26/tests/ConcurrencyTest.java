import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class ConcurrencyTest {

    @Test void case01() {
        assertThrows(IllegalArgumentException.class, () -> new ConcurrencyRound.ExposureLimiter(0));
    }

    @Test void case02() {
        var limiter = new ConcurrencyRound.ExposureLimiter(100);
        assertEquals(new ConcurrencyRound.Decision(true, 60), limiter.reserve("A", 40));
        assertEquals(new ConcurrencyRound.Decision(true, 10), limiter.reserve("A", 50));
        assertEquals(90, limiter.used("A"));
    }

    @Test void case03() {
        var limiter = new ConcurrencyRound.ExposureLimiter(100);
        assertTrue(limiter.reserve("A", 80).accepted());
        assertEquals(new ConcurrencyRound.Decision(false, 20), limiter.reserve("A", 21));
        assertEquals(80, limiter.used("A"));
    }

    @Test void case04() {
        var limiter = new ConcurrencyRound.ExposureLimiter(100);
        limiter.reserve("A", 30);
        assertThrows(IllegalStateException.class, () -> limiter.release("A", 31));
        assertEquals(30, limiter.used("A"));
        limiter.release("A", 30);
        assertEquals(0, limiter.used("A"));
    }

    @Test void case05() throws Exception {
        var limiter = new ConcurrencyRound.ExposureLimiter(100);
        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 40; i++) {
                futures.add(pool.submit(() -> {
                    start.await();
                    return limiter.reserve("A", 10).accepted();
                }));
            }
            start.countDown();
            int accepted = 0;
            for (Future<Boolean> future : futures) if (future.get()) accepted++;
            assertEquals(10, accepted);
            assertEquals(100, limiter.used("A"));
        } finally {
            pool.shutdownNow();
        }
    }

    @Test void case06() {
        var limiter = new ConcurrencyRound.ExposureLimiter(25);
        assertTrue(limiter.reserve("A", 25).accepted());
        assertTrue(limiter.reserve("B", 25).accepted());
        assertEquals(25, limiter.used("A"));
        assertEquals(25, limiter.used("B"));
        assertThrows(IllegalArgumentException.class, () -> limiter.reserve(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> limiter.release("A", 0));
    }
}
