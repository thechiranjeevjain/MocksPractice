# Round 10 — Database and Distributed Systems

Target: 10 minutes

## Scenario

An order service commits an order transition to PostgreSQL and publishes an event to a broker. Consumers update exposure and audit views. Producers and consumers may crash at any point; delivery is at least once. One large account becomes a hot key.

## Question 1

Design the write and publish boundary so a committed transition is not silently lost. Explain failure windows, transaction boundaries, retries, and operational recovery.

### Candidate answer

TODO

## Question 2

Define the idempotency identity and durable state needed by the exposure consumer. Explain why broker deduplication alone is insufficient and how concurrent duplicates are handled.

### Candidate answer

TODO

## Question 3

The hot account overloads one partition while cluster CPU remains moderate. Give two mitigation options and state which invariants each option makes harder to preserve.

### Candidate answer

TODO
