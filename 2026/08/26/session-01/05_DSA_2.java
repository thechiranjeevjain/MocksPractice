import java.util.List;

class DsaProblem02 {

    /**
     * Track activity counts under repeated updates.
     *
     * Contract:
     * - record(id) increments a nonblank id.
     * - retract(id) decrements it; absent ids throw IllegalStateException and count zero is removed.
     * - leaders(k) returns up to k ids ordered by count descending, then id ascending.
     * - k must be positive.
     * - update operations should be O(log U) or better, and leaders should avoid sorting all U ids.
     * - U is the number of currently distinct ids and may reach 500,000.
     */
    static final class ActivityTracker {

        ActivityTracker() {
            // TODO: Candidate implementation.
        }

        void record(String id) {
            // TODO: Candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }

        void retract(String id) {
            // TODO: Candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }

        List<String> leaders(int k) {
            // TODO: Candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }
    }
}
