import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

class LldRound {

    record Decision(boolean allowed, double remainingTokens) {}

    /*
     * SIX-PART GENERATOR — fill before code
     * TRIGGER:
     * PATTERN:
     * INVARIANT:
     * TEMPLATE:
     * FALLBACK:
     * OPTIMIZATION:
     */

    /**
     * Implement an in-memory per-client token-bucket limiter.
     *
     * - capacity and refillTokensPerSecond must be positive;
     * - clientId must be nonblank and requestedTokens must be positive;
     * - a new client starts full;
     * - refill is lazy using monotonic nanoseconds from the injected clock;
     * - a request is atomic per client; different clients should progress independently;
     * - tokens never exceed capacity and a rejected request consumes nothing;
     * - no background thread is required.
     */
    static final class TokenBucketRateLimiter {
        private final double capacity;
        private final double refillTokensPerSecond;
        private final LongSupplier nanoClock;
        private final ConcurrentHashMap<String, Bucket> buckets;

        private static final class Bucket {
            double tokens;
            long lastRefillNanos;

            Bucket(double tokens, long lastRefillNanos) {
                this.tokens = tokens;
                this.lastRefillNanos = lastRefillNanos;
            }
        }

        TokenBucketRateLimiter(double capacity, double refillTokensPerSecond, LongSupplier nanoClock) {
            this.capacity = capacity;
            this.refillTokensPerSecond = refillTokensPerSecond;
            this.nanoClock = nanoClock;
            this.buckets = null;
            // TODO: candidate implementation.
        }

        Decision tryAcquire(String clientId, double requestedTokens) {
            // TODO: candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }
    }
}
