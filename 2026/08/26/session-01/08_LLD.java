import java.util.List;

class LldRound {

    record Notification(String eventId, String type, String payload) {}

    interface DeliveryTarget {
        String name();
        boolean supports(String type);
        void deliver(Notification notification) throws Exception;
    }

    record DispatchReport(int delivered, List<String> failedTargets, boolean duplicate) {}

    /**
     * Design and implement the Router.
     *
     * Acceptance behavior:
     * - constructor rejects null targets, null entries, and duplicate/blank target names;
     * - dispatch rejects null notification and blank eventId/type;
     * - one process may call dispatch concurrently;
     * - a given eventId is delivered at most once across concurrent/repeated calls;
     * - all registered targets supporting the type are attempted in registration order;
     * - one target failure does not stop other targets;
     * - report contains successful count and failed target names in registration order;
     * - a repeated event returns duplicate=true, performs no delivery, and returns empty failures;
     * - internal state must not be affected by later mutation of the caller's target list.
     *
     * Keep the design as simple as the requirements allow. In a comment, identify the
     * persistence/distributed seam and one trade-off in your in-process idempotency policy.
     */
    static final class Router {

        Router(List<DeliveryTarget> targets) {
            // TODO: Candidate implementation.
        }

        DispatchReport dispatch(Notification notification) {
            // TODO: Candidate implementation.
            throw new UnsupportedOperationException("Candidate implementation required");
        }
    }
}
