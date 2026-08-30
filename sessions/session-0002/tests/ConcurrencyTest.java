import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class ConcurrencyTest {

    @Test void acceptsStrictlyIncreasingSequences() {
        var gate = new ConcurrencyRound.SequenceGate();
        assertTrue(gate.accept("orders", 4));
        assertTrue(gate.accept("orders", 9));
        assertEquals(9, gate.lastAccepted("orders"));
    }

    @Test void rejectsDuplicateAndStaleWithoutChangingState() {
        var gate = new ConcurrencyRound.SequenceGate();
        assertTrue(gate.accept("orders", 10));
        assertFalse(gate.accept("orders", 10));
        assertFalse(gate.accept("orders", 7));
        assertEquals(10, gate.lastAccepted("orders"));
    }

    @Test void streamsAreIndependent() {
        var gate = new ConcurrencyRound.SequenceGate();
        assertTrue(gate.accept("A", 100));
        assertTrue(gate.accept("B", 1));
        assertEquals(100, gate.lastAccepted("A"));
        assertEquals(1, gate.lastAccepted("B"));
        assertEquals(-1, gate.lastAccepted("missing"));
    }

    @Test void validatesInputs() {
        var gate = new ConcurrencyRound.SequenceGate();
        assertThrows(IllegalArgumentException.class, () -> gate.accept(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> gate.accept("A", -1));
        assertThrows(IllegalArgumentException.class, () -> gate.lastAccepted(null));
    }

    @Test void concurrentDuplicateIsAcceptedExactlyOnce() throws Exception {
        var gate = new ConcurrencyRound.SequenceGate();
        ExecutorService pool = Executors.newFixedThreadPool(12);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 40; i++) {
                futures.add(pool.submit(() -> { start.await(); return gate.accept("A", 7); }));
            }
            start.countDown();
            int accepted = 0;
            for (Future<Boolean> future : futures) if (future.get()) accepted++;
            assertEquals(1, accepted);
            assertEquals(7, gate.lastAccepted("A"));
        } finally {
            pool.shutdownNow();
        }
    }

    @Test void finalStateIsMaximumConcurrentSequence() throws Exception {
        var gate = new ConcurrencyRound.SequenceGate();
        ExecutorService pool = Executors.newFixedThreadPool(12);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        try {
            for (int sequence = 0; sequence <= 100; sequence++) {
                int value = sequence;
                futures.add(pool.submit(() -> { start.await(); return gate.accept("A", value); }));
            }
            start.countDown();
            for (Future<Boolean> future : futures) future.get();
            assertEquals(100, gate.lastAccepted("A"));
        } finally {
            pool.shutdownNow();
        }
    }
}
