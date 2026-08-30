# Repository Index

Last semantic refresh: 2026-08-30

## DSA

Source: `G:\TechStudyNotes\Codes\DSA10days`

- Java solutions and interview-facing active-recall material now expose a generated 21-family taxonomy across hashing, binary/answer search, windows, prefix techniques, linked lists, two pointers, tree/graph traversal, topological ordering, shortest paths, dynamic programming, backtracking, monotonic structures, heaps, intervals, tries, union-find, math/string, and design data structures.
- Highest-ROI material includes sum families, binary search, unique-window/string problems, prefix/suffix products, linked-list fundamentals, tree traversal/validation, component and prerequisite graphs, heap selection, monotonic structures, and core DP.
- The interview cockpit explicitly practices: brute force -> bottleneck -> pattern -> invariant -> code -> dry run.
- Additive pattern labs provide blank reusable frames for binary search, sliding window, two pointers, dynamic programming, and backtracking. The latest refresh adds N-Queens, Sudoku, and a moved anagram implementation. These are newly exposed, not mastered.
- Review state exists at `review/review.json`; source Java under `src/main/java/org/chijai` remains canonical.

## LLD

Source: `G:\TechStudyNotes\LLDProjects`

- The live 2026-08-30 source has been aggressively reduced toward interview-sized cores. LRU cache, DesignOrderBook, FIX gateway, matching/risk/OMS, and service-design examples are present again in smaller forms. Treat these as exposure and previous implementation only.
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
- Spring Boot, Java Streams/functional APIs, Docker, Kubernetes, and AWS-adjacent architecture are discoverable across the HLD corpus and are now mandatory rotation areas rather than optional follow-ups.
- The candidate dossier fallback contributes 27 Markdown/JSON files for project and behavioral anchoring. A dedicated resume and three-year performance-review dossier has not yet been located.
- `Gen4Projects/CJOfficeNotes` is configured as an optional Markdown-only source but is currently unavailable; no question may assume its missing contents.
- This index records exposure only. There is not yet independent mock evidence for recall, implementation reliability, explanation, defense, or adaptation.

## Latest change detection

The 2026-08-30 snapshot contains 2,268 included source files: DSA 294, LLD 165, HLD 1,782, and candidate dossier 27. The main change is LLD scope reduction/restructuring plus restored interview-core LRU/order-book/FIX material; DSA added backtracking and window material. These are exposure changes only. The already frozen session remains unchanged apart from layout/operational metadata.
