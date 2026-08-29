import java.util.Map;

class ConcurrencyRound {

    record Decision(boolean accepted, long remainingCapacity) {}

    /**
     * Maintain one independent quantity limit per account.
     *
     * Contract:
     * - limitPerAccount must be positive.
     * - reserve/release quantity must be positive; accountId must be nonblank.
     * - a successful reservation is atomic and may never take usage above the limit.
     * - a rejected reservation changes no state.
     * - releasing more than current usage throws IllegalStateException and changes no state.
     * - used(accountId) returns a thread-safe current value, or zero for an unseen account.
     * - operations for different accounts should not require one global critical section.
     */
    static final class ExposureLimiter {
        private final long limitPerAccount;
        private final Map<String, Long> usage;

        ExposureLimiter(long limitPerAccount) {
            this.limitPerAccount = limitPerAccount;
            this.usage = null;
            // TODO: Candidate implementation.
        }

        Decision reserve(String accountId, long quantity) {
            // TODO: Candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }

        void release(String accountId, long quantity) {
            // TODO: Candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }

        long used(String accountId) {
            // TODO: Candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }
    }
}
