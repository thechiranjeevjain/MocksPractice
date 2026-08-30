# Round 10 — Spring Framework, Spring Boot, Docker, Kubernetes, and AWS

Use the notification platform from Round 9. Keep answers operational and medium-depth; obscure commands are not required.

1. Where would you put the transaction boundary when accepting a notification and recording an outbox event? Explain why calling an `@Transactional` method through `this` may not start a transaction.
2. What should be constructor-injected? What should be configuration properties? How do bean scopes affect thread safety?
3. Distinguish liveness, readiness, and startup checks. Which dependencies must readiness include, and which should it avoid?
4. Describe a small production Docker image and why the process should not run as root.
5. Sketch the Kubernetes Deployment, Service, ConfigMap/Secret use, resource requests/limits, rolling update, and HPA signal.
6. Map the design to ECR, EKS, RDS, ElastiCache, MSK, S3, and IAM. State which are essential initially versus later.
7. A rollout is healthy by liveness but receives no traffic; Kafka lag and DB connections rise. Give a diagnosis order and safe rollback/mitigation plan.
