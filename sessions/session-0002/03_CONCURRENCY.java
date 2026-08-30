import java.util.concurrent.ConcurrentHashMap;

class ConcurrencyRound {

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
     * Accept a sequence number only when it is strictly greater than the last accepted
     * value for that stream.
     *
     * Contract:
     * - streamId must be nonblank and sequence must be non-negative;
     * - first valid sequence for a stream is accepted;
     * - check-and-update is atomic per stream;
     * - concurrent duplicate/stale sequences are rejected;
     * - streams must not share one global critical section;
     * - lastAccepted returns -1 for an unseen stream.
     */
    static final class SequenceGate {
        private final ConcurrentHashMap<String, Long> lastByStream;

        SequenceGate() {
            this.lastByStream = null;
            // TODO: candidate implementation.
        }

        boolean accept(String streamId, long sequence) {
            // TODO: candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }

        long lastAccepted(String streamId) {
            // TODO: candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }
    }
}
