# Round 9 — HLD: Notification Platform

Design a multi-tenant notification platform supporting email, SMS, and push.

## Functional requirements

- synchronous acceptance API with client idempotency key;
- scheduled and immediate delivery;
- tenant templates and channel preferences;
- provider retry with bounded exponential backoff;
- dead-letter handling and operator replay;
- delivery-status query and webhook/callback updates.

## Scale assumptions

- 20 million accepted notifications/day;
- normal 2,000 deliveries/second, burst 15,000/second;
- 30-day searchable metadata, longer audit retention;
- a provider or one tenant must not stall everyone else.

From a blank board, cover requirements, estimates, API/data model, components, queues/partitioning, idempotency, retries/DLQ, tenant fairness, observability, security, and failure recovery. State what is at-least-once and where you prevent duplicate user-visible delivery.

End with one 10x-load change and one tempting component you would defer.
