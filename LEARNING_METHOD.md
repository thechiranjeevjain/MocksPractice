# 80:20 Interview Learning Method

The unit of progress is cold reconstruction under interview conditions, not time studied or repository size.

## Ranked rules

1. **Try before looking.** Spend 2-10 minutes retrieving or solving before opening notes, solutions, hints, or AI. AI comes after cognitive effort.
2. **Close -> recall -> check.** Reconstruct from a blank page, IDE, or whiteboard; compare afterward. Only missing pieces enter the study list.
3. **Perform, do not merely explain.** Code from scratch, design from a blank board, and explain Java/JVM mechanisms plus trade-offs aloud.
4. **Review through retrieval.** A simple fallback rhythm is roughly 1, 3, and 7 days, but Review OS FSRS dates take precedence because they adapt to actual ratings.
5. **Store the generator, not an answer transcript.** Compress each durable topic into: `Trigger -> Pattern -> Invariant -> Template -> Fallback -> Optimization`.
6. **Fix gaps, do not restudy everything.** If 80% was reconstructed, focus on the missing 20%.
7. **Solve one meaningful variation after the anchor.** Change one constraint to distinguish transferable understanding from memorization.
8. **Use analogy only as a retrieval hook.** It is not a substitute for the technical mechanism.
9. **Keep a tiny failure log.** Store only recurring, actionable misses.
10. **Measure cold reconstruction.** Prefer “17/20 reconstructed cold” over “six hours studied.”

## Minimal loop

`COLD ATTEMPT -> LEARN -> CLOSE -> RECALL -> CHECK GAPS -> RETRY -> PERFORM -> SPACE`

## Desk rule

> Try before looking. Recall before rereading. Fix gaps, not everything. Learn patterns, not answers. AI after attempt. If I cannot reconstruct it cold, I do not own it yet.

## How this workspace enforces it

- A frozen mock is the cold attempt; candidate files contain no answer key or pattern leak.
- `DONE` starts audit and viva before teaching or final corrections.
- Only the top three demonstrated gaps enter `.interviewer/FAILURE_LOG.md` and the Review OS correction deck.
- Each correction card is retrieved cold before its source is opened.
- Variations change one meaningful constraint while preserving the competency being tested.
- Mastery changes only from repeated performance evidence.
