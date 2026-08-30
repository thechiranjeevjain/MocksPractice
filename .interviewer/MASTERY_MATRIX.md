# Mastery Matrix

Mastery is binary. `SOLID` requires two clean spaced reconstructions; a later retrieval/invariant failure regresses it to `NOT-SOLID`. `UNASSESSED` is not counted in backlog `B` until first tested. Repository exposure never changes mastery.

`NEW_GATE = 3`; introduce at most one new pattern per session and only while `B <= 3`.

| Pattern / competency | State | Clean spaced reconstructions | Last evidence | Regression trigger | Next lifecycle |
|---|---|---:|---|---|---|
| Arrays / strings | UNASSESSED | 0 | None | Any retrieval failure | Retrieval |
| Linked lists | UNASSESSED | 0 | None | Any retrieval failure | Retrieval |
| Trees / graphs | UNASSESSED | 0 | None | Any retrieval failure | Retrieval |
| Heap / intervals | UNASSESSED | 0 | None | Any retrieval failure | Retrieval |
| Dynamic programming / backtracking | UNASSESSED | 0 | None | Any retrieval failure | Retrieval |
| Java / JVM | UNASSESSED | 0 | None | Mechanism/invariant failure | Retrieval |
| Streams / functional | UNASSESSED | 0 | None | Pipeline/semantic failure | Retrieval |
| Concurrency | UNASSESSED | 0 | None | Safety/visibility failure | Retrieval |
| Spring Boot | UNASSESSED | 0 | None | Mechanism/trade-off failure | Retrieval |
| Docker / Kubernetes / AWS | UNASSESSED | 0 | None | Operational model failure | Retrieval |
| SQL / databases | UNASSESSED | 0 | None | Correctness/trade-off failure | Retrieval |
| Distributed systems | UNASSESSED | 0 | None | Failure-mode/invariant failure | Retrieval |
| LLD | UNASSESSED | 0 | None | Responsibility/invariant failure | Retrieval |
| HLD | UNASSESSED | 0 | None | Capacity/failure reasoning failure | Retrieval |
| Project depth | UNASSESSED | 0 | None | Unsupported ownership claim | Retrieval |
| Behavioral / leadership | UNASSESSED | 0 | None | Unsubstantiated/unclear evidence | Retrieval |

Current `B = 0` because there is no completed mock evidence.
