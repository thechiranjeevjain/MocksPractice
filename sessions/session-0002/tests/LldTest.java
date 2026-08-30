import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class LldTest {
    @Test void newClientStartsFullAndRejectDoesNotConsume() {
        AtomicLong clock = new AtomicLong();
        var limiter = new LldRound.TokenBucketRateLimiter(5, 2, clock::get);
        assertEquals(new LldRound.Decision(true, 2.0), limiter.tryAcquire("A", 3));
        assertEquals(new LldRound.Decision(false, 2.0), limiter.tryAcquire("A", 3));
        assertEquals(new LldRound.Decision(true, 0.0), limiter.tryAcquire("A", 2));
    }

    @Test void lazyRefillUsesElapsedMonotonicTime() {
        AtomicLong clock = new AtomicLong();
        var limiter = new LldRound.TokenBucketRateLimiter(5, 2, clock::get);
        limiter.tryAcquire("A", 5);
        clock.addAndGet(500_000_000L);
        assertEquals(new LldRound.Decision(true, 0.0), limiter.tryAcquire("A", 1));
    }

    @Test void refillIsCappedAtCapacity() {
        AtomicLong clock = new AtomicLong();
        var limiter = new LldRound.TokenBucketRateLimiter(5, 2, clock::get);
        limiter.tryAcquire("A", 4);
        clock.addAndGet(100_000_000_000L);
        assertEquals(new LldRound.Decision(true, 0.0), limiter.tryAcquire("A", 5));
    }

    @Test void clientsAreIndependent() {
        AtomicLong clock = new AtomicLong();
        var limiter = new LldRound.TokenBucketRateLimiter(2, 1, clock::get);
        assertTrue(limiter.tryAcquire("A", 2).allowed());
        assertTrue(limiter.tryAcquire("B", 2).allowed());
    }

    @Test void validatesInputs() {
        assertThrows(IllegalArgumentException.class, () -> new LldRound.TokenBucketRateLimiter(0, 1, System::nanoTime));
        assertThrows(IllegalArgumentException.class, () -> new LldRound.TokenBucketRateLimiter(1, 0, System::nanoTime));
        assertThrows(IllegalArgumentException.class, () -> new LldRound.TokenBucketRateLimiter(1, 1, null));
        var limiter = new LldRound.TokenBucketRateLimiter(1, 1, System::nanoTime);
        assertThrows(IllegalArgumentException.class, () -> limiter.tryAcquire(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> limiter.tryAcquire("A", 0));
    }

    @Test void concurrentRequestsCannotOverspend() throws Exception {
        AtomicLong clock = new AtomicLong();
        var limiter = new LldRound.TokenBucketRateLimiter(5, 1, clock::get);
        ExecutorService pool = Executors.newFixedThreadPool(12);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 30; i++) {
                futures.add(pool.submit(() -> { start.await(); return limiter.tryAcquire("A", 1).allowed(); }));
            }
            start.countDown();
            int allowed = 0;
            for (Future<Boolean> future : futures) if (future.get()) allowed++;
            assertEquals(5, allowed);
        } finally {
            pool.shutdownNow();
        }
    }
}
