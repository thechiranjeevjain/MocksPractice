import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LldTest {
    static final class Target implements LldRound.DeliveryTarget {
        private final String name;
        private final String type;
        private final boolean fail;
        private final AtomicInteger calls = new AtomicInteger();

        Target(String name, String type, boolean fail) { this.name = name; this.type = type; this.fail = fail; }
        public String name() { return name; }
        public boolean supports(String candidate) { return type.equals(candidate); }
        public void deliver(LldRound.Notification notification) throws Exception {
            calls.incrementAndGet();
            if (fail) throw new Exception("expected test failure");
        }
    }

    @Test void case01() {
        var a = new Target("a", "TRADE", false);
        var b = new Target("b", "TRADE", false);
        var router = new LldRound.Router(List.of(a, b));
        assertEquals(new LldRound.DispatchReport(2, List.of(), false),
                router.dispatch(new LldRound.Notification("e1", "TRADE", "p")));
        assertEquals(1, a.calls.get()); assertEquals(1, b.calls.get());
    }

    @Test void case02() {
        var a = new Target("a", "TRADE", true);
        var b = new Target("b", "TRADE", false);
        var router = new LldRound.Router(List.of(a, b));
        assertEquals(new LldRound.DispatchReport(1, List.of("a"), false),
                router.dispatch(new LldRound.Notification("e1", "TRADE", "p")));
        assertEquals(1, b.calls.get());
    }

    @Test void case03() {
        var a = new Target("a", "TRADE", false);
        var router = new LldRound.Router(List.of(a));
        router.dispatch(new LldRound.Notification("e1", "TRADE", "p"));
        assertEquals(new LldRound.DispatchReport(0, List.of(), true),
                router.dispatch(new LldRound.Notification("e1", "TRADE", "p2")));
        assertEquals(1, a.calls.get());
    }

    @Test void case04() throws Exception {
        var target = new Target("a", "TRADE", false);
        var router = new LldRound.Router(List.of(target));
        ExecutorService pool = Executors.newFixedThreadPool(12);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<LldRound.DispatchReport>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 30; i++) futures.add(pool.submit(() -> {
                start.await();
                return router.dispatch(new LldRound.Notification("same", "TRADE", "p"));
            }));
            start.countDown();
            int first = 0;
            for (Future<LldRound.DispatchReport> future : futures) if (!future.get().duplicate()) first++;
            assertEquals(1, first);
            assertEquals(1, target.calls.get());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test void case05() {
        var mutable = new ArrayList<LldRound.DeliveryTarget>();
        var a = new Target("a", "TRADE", false);
        mutable.add(a);
        var router = new LldRound.Router(mutable);
        mutable.add(new Target("b", "TRADE", false));
        assertEquals(1, router.dispatch(new LldRound.Notification("e1", "TRADE", "p")).delivered());
    }

    @Test void case06() {
        assertThrows(IllegalArgumentException.class, () -> new LldRound.Router(null));
        assertThrows(IllegalArgumentException.class, () -> new LldRound.Router(List.of(
                new Target("same", "A", false), new Target("same", "B", false))));
        var router = new LldRound.Router(List.of());
        assertThrows(IllegalArgumentException.class, () ->
                router.dispatch(new LldRound.Notification(" ", "A", "p")));
    }
}
