import java.util.List;

class DsaProblem03 {

    record Dependency(String before, String after) {}

    /**
     * Build an execution plan containing every task exactly once.
     * Each dependency requires before to appear earlier than after.
     * When more than one next task is valid, choose the lexicographically smallest id.
     * Return an empty list when no complete plan exists.
     *
     * Inputs are non-null. Task ids are unique and nonblank. Every dependency references
     * a supplied task; duplicate dependency rows may appear. There may be 200,000 tasks
     * and 500,000 dependency rows.
     */
    static List<String> buildPlan(List<String> tasks, List<Dependency> dependencies) {
        // TODO: Candidate implementation.
        throw new UnsupportedOperationException("Candidate implementation required");
    }
}
