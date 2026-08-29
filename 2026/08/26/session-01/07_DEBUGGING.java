import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DebuggingRound {

    record Execution(String orderId, String venue, long quantity) {}
    record Aggregate(String orderId, String venue, long quantity) {}

    /**
     * Required behavior:
     * - aggregate by the composite (orderId, venue) identity;
     * - support totals larger than Integer.MAX_VALUE;
     * - return quantity descending, then orderId ascending, then venue ascending;
     * - reject null input/list elements and blank fields;
     * - do not mutate the caller's list.
     *
     * The implementation below compiles but is defective. Identify the defects, fix them,
     * and add a short justification in CANDIDATE_NOTES below.
     */
    static List<Aggregate> aggregate(List<Execution> executions) {
        Map<String, Integer> totals = new HashMap<>();
        for (Execution execution : executions) {
            totals.merge(execution.orderId(), (int) execution.quantity(), Integer::sum);
        }

        List<Aggregate> result = new ArrayList<>();
        totals.forEach((orderId, quantity) -> result.add(new Aggregate(orderId, "ANY", quantity)));
        result.sort((left, right) -> (int) (right.quantity() - left.quantity()));
        return result;
    }

    /*
     * CANDIDATE_NOTES
     * TODO
     */
}
