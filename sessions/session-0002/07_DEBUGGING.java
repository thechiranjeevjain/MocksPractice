import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DebuggingRound {

    record Fill(String orderId, String desk, long quantity, long priceCents) {}
    record DeskTotal(String desk, long notional) {}

    /**
     * Return totals for the requested desk, sorted by notional descending and desk
     * ascending. Duplicate fills for the same order are all legitimate and must be summed.
     *
     * The implementation contains correctness bugs. Diagnose and repair them without
     * changing the signature or records.
     */
    static List<DeskTotal> summarize(List<Fill> fills, String requestedDesk) {
        Map<String, Integer> totals = new HashMap<>();
        for (Fill fill : fills) {
            if (fill.desk() == requestedDesk) {
                int notional = (int) (fill.quantity() * fill.priceCents());
                totals.put(fill.orderId(), notional);
            }
        }

        List<DeskTotal> result = new ArrayList<>();
        totals.forEach((desk, total) -> result.add(new DeskTotal(desk, total)));
        result.sort(Comparator.comparingLong(DeskTotal::notional).reversed());
        return result;
    }
}
