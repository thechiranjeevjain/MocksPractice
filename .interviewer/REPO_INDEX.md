# Repository Index

Last semantic refresh: 2026-08-29

## DSA

Source: `G:\TechStudyNotes\Codes\DSA10days`

- Java solutions and interview-facing active-recall material now expose a generated 21-family taxonomy across hashing, binary/answer search, windows, prefix techniques, linked lists, two pointers, tree/graph traversal, topological ordering, shortest paths, dynamic programming, backtracking, monotonic structures, heaps, intervals, tries, union-find, math/string, and design data structures.
- Highest-ROI material includes sum families, binary search, unique-window/string problems, prefix/suffix products, linked-list fundamentals, tree traversal/validation, component and prerequisite graphs, heap selection, monotonic structures, and core DP.
- The interview cockpit explicitly practices: brute force -> bottleneck -> pattern -> invariant -> code -> dry run.
- New additive pattern labs provide blank reusable frames for binary search, sliding window, two pointers, and dynamic programming, with a project/pattern tree and active 90/extension plans. The latest source refresh also adds/expands minimum-valid coverage accounting and maximum-valid repair-budget sliding-window chapters. These additions are newly exposed, not mastered.
- Review state exists at `review/review.json`; source Java under `src/main/java/org/chijai` remains canonical.

## LLD

Source: `G:\TechStudyNotes\LLDProjects`

- The repository index still describes sixteen runnable Java interview projects, but the 2026-08-28 live scan finds the `lru-cache` implementation/docs deleted while its folder and README references remain. Treat LRU as DSA/design exposure, not as a currently runnable LLD proof, until that repository state is reconciled.
- Recurring competencies: domain modeling, state machines, price-time priority, partial fills, atomic reservation/rollback, sequencing/replay, idempotency, adapters, strategy boundaries, locking, focused tests, and production seams.
- Trading-focused progression includes matching engine, risk engine, OMS, FIX session manager, and exchange gateway.
- The repository distinguishes a 40-60 minute interview code slice from production-shaped implementations.

## HLD

Source: `G:\TechStudyNotes\SystemDesignProjects`

- Thirty canonical runnable projects progress from web/cache/queue/KV/concurrency foundations through services, workflows, distributed correctness, and domain specialization.
- Distributed-system coverage includes caching, queues, idempotency, retries, dead letters, transactions, event logs, replication, partitioning, recovery, observability, security, and readiness boundaries.
- Trading portfolio includes exchange-lite, trading risk, mini risk management, exchange connectivity, market data, and electronic trading platforms.
- Market-data material covers strict sequencing, gap detection/retransmission, normalization, book reconstruction, symbol partitioning, bounded fan-out, conflate/disconnect policy, and slow-consumer isolation.
- Shared HLD guidance uses requirements -> estimates -> design -> failure analysis and separates interview proof from production proof.

## Cross-cutting exposure

- Java/Spring/Maven implementations, testing, concurrency, JVM/performance reasoning, SQL/data models, distributed reliability, production diagnosis, and electronic-trading constraints are well represented.
- This index records exposure only. There is not yet independent mock evidence for recall, implementation reliability, explanation, defense, or adaptation.

## Latest change detection

The 2026-08-29 snapshot contains 2,299 included source files: DSA 292, LLD 225, and HLD 1,782. The final incremental reconciliation detected `LongestRepeatingCharacterReplacement.java` as added and `MinimumWindowSubstring.java` as modified after the prior pattern-lab refresh. These are exposure changes only; the active mock competencies remain valid and the frozen candidate paper was not adapted after publication.
