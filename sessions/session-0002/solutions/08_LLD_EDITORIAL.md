# Round 8 Editorial — Per-Key Token Bucket

## TRIGGER

Permit bursts up to a capacity while enforcing an average refill rate independently per client.

## PATTERN

Per-key token bucket with lazy time-based refill and atomic map mutation.

## INVARIANT

For each client, `0 <= tokens <= capacity`; one accepted request subtracts exactly its cost, and a rejection subtracts nothing.

## TEMPLATE

Inject monotonic clock -> `ConcurrentHashMap.compute` -> refill from elapsed time -> cap -> decide/subtract -> return snapshot.

## FALLBACK

A fixed-window counter is simpler but permits boundary bursts. One background refill thread adds lifecycle complexity and still needs synchronization.

## OPTIMIZATION

Refill lazily only when a client is accessed and use per-key atomic compute instead of a global lock.

## Final implementation / answer

```java
static final class TokenBucketRateLimiter {
    private final double capacity;
    private final double refillTokensPerSecond;
    private final LongSupplier nanoClock;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final class Bucket {
        double tokens;
        long lastRefillNanos;
        Bucket(double tokens, long lastRefillNanos) {
            this.tokens = tokens;
            this.lastRefillNanos = lastRefillNanos;
        }
    }

    TokenBucketRateLimiter(double capacity, double rate, LongSupplier nanoClock) {
        if (!Double.isFinite(capacity) || capacity <= 0 ||
                !Double.isFinite(rate) || rate <= 0 || nanoClock == null)
            throw new IllegalArgumentException("positive finite capacity/rate and clock required");
        this.capacity = capacity;
        this.refillTokensPerSecond = rate;
        this.nanoClock = nanoClock;
    }

    Decision tryAcquire(String clientId, double requestedTokens) {
        if (clientId == null || clientId.isBlank() ||
                !Double.isFinite(requestedTokens) || requestedTokens <= 0)
            throw new IllegalArgumentException("valid client and token count required");

        long now = nanoClock.getAsLong();
        var result = new java.util.concurrent.atomic.AtomicReference<Decision>();
        buckets.compute(clientId, (id, existing) -> {
            Bucket bucket = existing == null ? new Bucket(capacity, now) : existing;
            long elapsed = Math.max(0L, now - bucket.lastRefillNanos);
            bucket.tokens = Math.min(capacity,
                    bucket.tokens + elapsed / 1_000_000_000.0 * refillTokensPerSecond);
            bucket.lastRefillNanos = Math.max(bucket.lastRefillNanos, now);
            boolean allowed = bucket.tokens + 1e-12 >= requestedTokens;
            if (allowed) bucket.tokens = Math.max(0.0, bucket.tokens - requestedTokens);
            result.set(new Decision(allowed, bucket.tokens));
            return bucket;
        });
        return result.get();
    }
}
```

The distributed seam replaces in-memory buckets with an atomic external-state mechanism or partitions clients so one owner processes each key. That adds network latency and failure semantics.

## Complexity

Expected O(1) time per acquire and O(c) space for c observed clients.

## Edge cases

New client, long idle refill, rejected request, floating precision, clock regression, invalid/huge input, and concurrent same-key requests.

## Observed candidate mistakes

None in this sample. Common misses are wall-clock time, uncapped refill, and non-atomic get/update/put.

## Next variation

Add one constraint: evict idle client buckets without racing active requests.
