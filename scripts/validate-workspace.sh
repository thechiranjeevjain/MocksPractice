#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
practice_root="$(cd -- "$script_dir/.." && pwd)"
required=(
  ".ai/INTERVIEW_AGENT.md" "LEARNING_METHOD.md" "FAILURE_LOG.md" "PROGRESS.md" "MASTERY_MATRIX.md"
  ".interviewer/REPO_INDEX.md" ".interviewer/CANDIDATE_MODEL.md" ".interviewer/INTERVIEWER_STATE.md"
  ".interviewer/SESSION_HISTORY.md" ".interviewer/EVIDENCE_LOG.md" "review/review.json" "config/repositories.json"
)
for relative in "${required[@]}"; do
  [[ -f "$practice_root/$relative" ]] || { echo "Missing required file: $relative" >&2; exit 1; }
done
bash "$script_dir/rebuild-profile.sh"
bash "$script_dir/review-os.sh" stats
echo "MocksPractice validation passed."
