# MocksPractice

Persistent, repo-aware Senior Software Engineer mock interviews grounded in the current DSA, LLD, and HLD repositories. Preparation files establish exposure; only independent mock and viva evidence changes mastery.

## Chat commands

- `START MOCK` — fresh scan, adaptive full interview generation, and immutable freeze.
- `DONE` — rescan, submission audit, tests, focused viva, final scoring, long-term updates, and FSRS correction scheduling.
- `REBUILD PROFILE` — refresh repository understanding without creating an exam.
- `HINT` / `NEXT` — minimal hint or next viva question.

Optional mode suffixes: `START MOCK Full`, `START MOCK Standard`, `START MOCK Compressed`, or `START MOCK Micro`. Every mode preserves the streak and contains at least one six-part cold reconstruction.

## Source repositories

- `G:\TechStudyNotes\Codes\DSA10days`
- `G:\TechStudyNotes\LLDProjects`
- `G:\TechStudyNotes\SystemDesignProjects`
- completed sessions under this repository as peer-practice evidence
- `Gen4Projects/CJOfficeNotes` through `MOCK_OFFICE_ROOT` when available (Markdown only)
- candidate dossier through `CANDIDATE_DOSSIER_ROOT`; current fallback is sibling `company-specific`

The paths above are the current Windows checkout. The portable default is a common parent directory:

```text
TechStudyNotes/
├── Codes/DSA10days/
├── LLDProjects/
├── SystemDesignProjects/
├── MocksPractice/
└── review-os/
```

On a different layout set `MOCK_DSA_ROOT`, `MOCK_LLD_ROOT`, `MOCK_HLD_ROOT`, and `REVIEW_OS_ROOT` to absolute native paths. This works with Windows paths, Linux paths, and macOS paths.

Optional inputs use `MOCK_OFFICE_ROOT` and `CANDIDATE_DOSSIER_ROOT`. Missing optional inputs are reported and never fabricated.

## Session layout

Sessions are flat and globally numbered across all dates and years:

```text
sessions/
├── session-0001-2026-08-26/
├── session-0002-2026-08-30/
└── session-0003-2026-09-02/
```

The next number is the highest number recorded in `.interviewer/SESSION_HISTORY.md` plus one. Numbers never reset or get reused. During an attempt, `solutions/` contains only `LOCKED.md`; `DONE` replaces it with one six-part editorial per round.

## Requirements

- Java 21 or newer
- The Maven Wrapper checked into `review-os`
- PowerShell on Windows; Bash on Linux/macOS

## Useful local commands

```powershell
.\scripts\rebuild-profile.cmd
.\scripts\sync-sources.cmd
.\scripts\build-cache.cmd
.\scripts\validate-workspace.cmd
.\scripts\review-due.cmd
.\scripts\freeze-session.cmd sessions\session-0001-2026-08-26
.\scripts\verify-freeze.cmd sessions\session-0001-2026-08-26
```

Linux/macOS:

```bash
bash ./scripts/rebuild-profile.sh
bash ./scripts/sync-sources.sh
bash ./scripts/build-cache.sh
bash ./scripts/validate-workspace.sh
bash ./scripts/review-due.sh
bash ./scripts/freeze-session.sh sessions/session-0001-2026-08-26
bash ./scripts/verify-freeze.sh sessions/session-0001-2026-08-26
```

The Bash workflow does not require PowerShell. Windows and Unix entrypoints invoke the same Java 21 source-file tools for repository scanning and session freezing, so manifests and path rules stay consistent across operating systems.

Run the Windows commands on Windows and `bash ./scripts/validate-workspace.sh` on each Unix target before release. This checkout has been validated with Windows PowerShell/CMD and Git Bash under Java 21. Native Linux/macOS execution remains a host-level acceptance check; the scripts themselves contain no PowerShell dependency or drive-letter requirement.

The daily entrypoint is [.ai/OPERATOR_QUICKCARD.md](.ai/OPERATOR_QUICKCARD.md); the full operating contract is [.ai/INTERVIEW_AGENT.md](.ai/INTERVIEW_AGENT.md). Machine snapshots support change detection; `.interviewer/REPO_INDEX.md` remains the semantic source model. See [.ai/FILE_MAP.md](.ai/FILE_MAP.md) and [.ai/DIAGRAMS.md](.ai/DIAGRAMS.md) for bounded navigation.

The default difficulty is medium `3.0-3.5`, calibrated for a `70-85%` no-red-flags win, with at most one stretch item and no zero-exposure curveballs.

The daily practice discipline is [LEARNING_METHOD.md](LEARNING_METHOD.md): cold attempt first, retrieve before rereading, fix only observed gaps, learn reusable generators, then space the next retrieval.
