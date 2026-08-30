# Senior Software Engineer Mock Interview

> **QUESTION-ONLY SAMPLE:** This frozen paper is retained as a non-scored example. It is not an active exam, creates no mastery evidence, and intentionally keeps `solutions/LOCKED.md` to demonstrate the anti-peek state. Its canonical path is `sessions/session-0001`; the date remains metadata below.

Date: 2026-08-26

Session: 0001
Target duration: 180 minutes

## Rules

- No AI assistance during the attempt.
- Try cold before looking: spend real effort retrieving and solving before requesting `HINT`.
- Do not inspect preparation solutions, previous solutions, or `.interviewer` files.
- Work directly inside the files in this directory.
- Run the provided visible tests where useful and debug independently.
- Write reasoning where requested; do not change question specifications or tests.
- Passing visible tests does not guarantee full marks.
- This question-only sample is frozen. Its `solutions/` folder intentionally remains locked; use the solved session-0002 sample to see editorial format.

## Suggested timing

| Round | Minutes |
|---|---:|
| Project deep dive | 8 |
| Java / JVM | 12 |
| Concurrency | 15 |
| DSA 1 | 20 |
| DSA 2 | 22 |
| DSA 3 | 22 |
| Debugging | 15 |
| LLD | 18 |
| HLD | 25 |
| Database / distributed systems | 10 |
| Behavioral / leadership | 13 |
| **Total** | **180** |

## Running tests

Use the commands under `scripts`, for example:

```powershell
.\scripts\test-dsa-1.cmd
.\scripts\test-all.cmd
```

Linux/macOS:

```bash
bash ./scripts/test-dsa-1.sh
bash ./scripts/test-all.sh
```

If `review-os` is not beside `MocksPractice`, set `REVIEW_OS_ROOT` to its absolute native path first.

The wrappers append objective outcomes to the central evidence log.

Desk rule: **try before looking; recall before rereading; if you cannot reconstruct it cold, you do not own it yet.**
