# MocksPractice

Persistent, repo-aware Senior Software Engineer mock interviews grounded in the current DSA, LLD, and HLD repositories. Preparation files establish exposure; only independent mock and viva evidence changes mastery.

## Chat commands

- `START MOCK` — fresh scan, adaptive full interview generation, and immutable freeze.
- `DONE` — rescan, submission audit, tests, focused viva, final scoring, long-term updates, and FSRS correction scheduling.
- `REBUILD PROFILE` — refresh repository understanding without creating an exam.
- `HINT` / `NEXT` — minimal hint or next viva question.

## Source repositories

- `G:\TechStudyNotes\Codes\DSA10days`
- `G:\TechStudyNotes\LLDProjects`
- `G:\TechStudyNotes\SystemDesignProjects`

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

## Requirements

- Java 21 or newer
- The Maven Wrapper checked into `review-os`
- PowerShell on Windows; Bash on Linux/macOS

## Useful local commands

```powershell
.\scripts\rebuild-profile.cmd
.\scripts\validate-workspace.cmd
.\scripts\review-due.cmd
.\scripts\freeze-session.cmd 2026\08\26\session-01
.\scripts\verify-freeze.cmd 2026\08\26\session-01
```

Linux/macOS:

```bash
bash ./scripts/rebuild-profile.sh
bash ./scripts/validate-workspace.sh
bash ./scripts/review-due.sh
bash ./scripts/freeze-session.sh 2026/08/26/session-01
bash ./scripts/verify-freeze.sh 2026/08/26/session-01
```

The Bash workflow does not require PowerShell. Windows and Unix entrypoints invoke the same Java 21 source-file tools for repository scanning and session freezing, so manifests and path rules stay consistent across operating systems.

Run the Windows commands on Windows and `bash ./scripts/validate-workspace.sh` on each Unix target before release. This checkout has been validated with Windows PowerShell/CMD and Git Bash under Java 21. Native Linux/macOS execution remains a host-level acceptance check; the scripts themselves contain no PowerShell dependency or drive-letter requirement.

The operating contract is [.ai/INTERVIEW_AGENT.md](.ai/INTERVIEW_AGENT.md). Machine snapshots support change detection; `.interviewer/REPO_INDEX.md` remains the semantic source model.

The daily practice discipline is [LEARNING_METHOD.md](LEARNING_METHOD.md): cold attempt first, retrieve before rereading, fix only observed gaps, learn reusable generators, then space the next retrieval.
