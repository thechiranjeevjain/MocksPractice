# Round 1 Editorial — Project Deep Dive

## TRIGGER

A broad “tell me about a project” prompt that tests ownership, architecture, trade-offs, and evidence.

## PATTERN

Boundary -> critical flow -> invariant/SLO -> failure -> trade-off -> evidence -> scale change.

## INVARIANT

Every claim must remain inside the candidate's real ownership boundary and be supported by concrete evidence.

## TEMPLATE

`Problem; I owned; flow; invariant; hardest failure; decision; evidence/result; 10x change.`

## FALLBACK

A component list is easy to recall but weak because it does not demonstrate decisions, correctness, or ownership.

## OPTIMIZATION

Use one critical path and one material trade-off. Go deeper instead of naming more technologies.

## Final answer guide

A strong 90-second opening states the user problem, exact ownership, scale/constraint, critical flow, and measurable result. The deeper answer should trace no more than eight steps and identify one invariant such as ordered processing, risk-limit safety, or idempotent acceptance. Explain a real failure mode, detection, recovery, and the rejected alternative. Say `I implemented`, `I contributed`, or `I studied` precisely.

## Complexity

Not algorithmic. Budget roughly 90 seconds for the summary and 6-8 minutes for evidence and follow-ups.

## Edge cases

- A polished repository is not proof of production ownership.
- Separate measured results from estimates.
- State unresolved limitations instead of claiming perfection.

## Observed candidate mistakes

None; this is a non-scored sample. Common red flags are architecture tours without decisions and ownership inflation.

## Next variation

Keep the same project but explain the first failure at 10x traffic.
