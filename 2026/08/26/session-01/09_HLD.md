# Round 9 — High-Level Design

Target: 25 minutes

## Problem

Design a multi-venue market-data platform that ingests sequenced order-level events from eight venues, reconstructs per-symbol books, and serves both latency-sensitive strategies and slower UI clients.

## Requirements

- Peak aggregate input: 2,000,000 events/second; normal peak is bursty and skewed.
- 150,000 symbols; the hottest 100 symbols may produce 35% of traffic.
- Strategy subscribers require trustworthy in-order updates with p99 under 8 ms inside one region.
- UI subscribers may receive conflated snapshots every 250 ms.
- Packets can be duplicated, reordered, or missing. A replay service exists but can be slow or unavailable.
- A bad symbol or slow subscriber must not stall unrelated symbols.
- Operators must know when a book is stale or untrustworthy.
- Support regional disaster recovery; state the recovery objective you choose.

## Candidate solution

### Clarifications / assumptions

TODO

### APIs / wire contracts

TODO

### Capacity estimates

TODO

### Data model and ownership

TODO

### High-level architecture

TODO

### Ingest, gap-recovery, and publish paths

TODO

### Partitioning, hot symbols, and scaling

TODO

### Consistency and ordering guarantees

TODO

### Availability and disaster recovery

TODO

### Failure handling, backpressure, and slow consumers

TODO

### Retries, duplicates, and idempotency

TODO

### Observability and stale-state signaling

TODO

### Security / entitlements

TODO

### Cost and trade-offs

TODO
