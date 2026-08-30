# Round 10 Editorial — Spring, Containers, Kubernetes, and AWS

## TRIGGER

Take a Spring service from code-level correctness to a deployable, observable, least-privilege runtime.

## PATTERN

Transactional service boundary -> immutable configuration/DI -> container -> Kubernetes health/resources/rollout -> managed AWS dependencies.

## INVARIANT

A reported-ready instance can safely accept traffic, and one accepted command cannot be lost between database commit and event publication.

## TEMPLATE

Transaction/proxy -> beans/config -> probes -> image -> workload/service -> AWS mapping -> diagnosis/rollback.

## FALLBACK

Run a fat JAR on one VM with in-process retries. It can demonstrate function but lacks repeatable rollout, isolation, autoscaling, and managed failure boundaries.

## OPTIMIZATION

Start with the fewest managed services that satisfy durability and operations; add caching/streaming only for measured needs.

## Final implementation / answer

1. Put `@Transactional` on a public application-service method that writes notification and outbox rows together. Spring's usual proxy intercepts calls entering through the proxy; `this.someTransactionalMethod()` bypasses that interception. Move the boundary to another injected bean or call through the proxy only when justified.
2. Constructor-inject required collaborators. Bind validated environment-specific values with `@ConfigurationProperties`. Singleton beans must be stateless or thread-safe; request/session scope adds lifecycle/proxy costs and should solve a real need.
3. Liveness answers “should this process restart?” and should not fail merely because DB/Kafka is temporarily down. Readiness answers “can this instance safely receive traffic?” and may include critical local initialization plus carefully bounded dependencies. Startup protects slow initialization from premature liveness failure.
4. Use a pinned JRE 21 runtime or buildpack image, non-root user, read-only filesystem where possible, explicit memory/container settings, no secrets in layers, and a simple Java entrypoint. Build/test in CI and push the immutable digest to ECR.
5. EKS runs a Deployment with multiple replicas, Service, ConfigMap for non-secret settings, Secret/external secret integration, requests/limits, readiness/liveness/startup probes, PodDisruptionBudget, rolling-update constraints, and HPA based on CPU plus queue lag/age when available.
6. ECR stores images; EKS runs workloads; RDS holds durable relational/outbox state; MSK carries events; ElastiCache serves measured cache/rate needs; S3 stores audit exports/templates when appropriate; IAM roles for service accounts grant only required actions. RDS is essential, while MSK/cache/S3 depend on the chosen initial scale and durability requirements.
7. If liveness is green but no traffic arrives, inspect readiness events and endpoint membership first, then Service selectors/ports, ingress, NetworkPolicy/security groups, configuration/secrets, DB pool saturation, and MSK lag. Stop/rollback the rollout, preserve durable backlog, reduce retry pressure, restore DB headroom, and verify readiness before resuming.

## Complexity

Operational rather than algorithmic. Keep synchronous dependency count small because tail latency and availability compound across calls.

## Edge cases

Self-invocation, readiness dependency cascades, missing resource requests, secret leakage, image tag drift, retry storm, and IAM over-permission.

## Observed candidate mistakes

None in this sample. Common issues are putting every dependency in liveness and listing AWS services without explaining why they exist.

## Next variation

Change one constraint: deploy with zero dropped accepted requests during a database schema migration.
