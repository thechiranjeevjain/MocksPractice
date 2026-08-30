# Round 9 Editorial — Notification Platform

## TRIGGER

Asynchronous multi-channel delivery with retries, scheduling, tenant isolation, and user-visible duplicate risk.

## PATTERN

Durable accept -> outbox/event -> partitioned channel workers -> provider adapter -> status/audit -> retry/DLQ.

## INVARIANT

One accepted idempotency key maps to one logical notification, and every delivery attempt has a durable, auditable state transition.

## TEMPLATE

Requirements -> estimates -> API/data -> accept path -> async path -> failure semantics -> scaling -> observability/security.

## FALLBACK

Call providers synchronously from the API. It is simple but couples latency/availability to providers and makes retries unsafe.

## OPTIMIZATION

Partition queues by tenant/notification key, isolate channels/providers, batch status writes where safe, and apply tenant quotas/backpressure.

## Final implementation / answer

`POST /notifications` accepts tenant, idempotency key, template/version, recipients, channels, schedule, and variables. In one RDS transaction, insert the notification plus outbox row under a unique `(tenant_id, idempotency_key)` constraint. Return the original identifier on retry.

An outbox relay publishes to MSK. A scheduler publishes due work. Partition by tenant plus stable key while enforcing per-tenant quotas. Channel workers render a versioned template, consult preferences, invoke provider adapters with timeouts/circuit breakers, and persist attempt state. Retriable failures use bounded exponential backoff with jitter; permanent/exhausted failures go to a DLQ. Operator replay creates an audited new attempt rather than rewriting history.

Delivery is at-least-once internally. Prevent duplicate user-visible sends with a stable provider idempotency key when supported and a durable send-attempt state machine when not. Webhook updates are independently idempotent.

Use RDS for metadata/idempotency, ElastiCache for short-lived configuration/rate data, MSK for durable event streams, and S3 for long-retention audit exports or large immutable payloads. Metrics cover acceptance latency/error, queue lag/age, provider latency/error, retries, DLQ, tenant throttling, and callback delay. Encrypt data, minimize recipient PII, enforce tenant authorization, and use least-privilege IAM.

At 10x, shard high-volume tenants, independently scale channel workers, and protect provider quotas. Defer global active-active unless recovery objectives require it.

## Complexity

Capacity target is driven by the 15,000/s burst, channel fan-out, provider quotas, and retention. Individual keyed operations should remain amortized O(1).

## Edge cases

API retry after timeout, provider accepts then times out, duplicate webhook, template changes after scheduling, poison message, tenant flood, and regional provider outage.

## Observed candidate mistakes

None in this sample. Common gaps are “exactly once” claims, retry without idempotency, and a shared queue with no tenant fairness.

## Next variation

Add one constraint: a tenant requires ordered notifications per recipient.
