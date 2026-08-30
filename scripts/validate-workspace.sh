#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
practice_root="$(cd -- "$script_dir/.." && pwd)"
required=(
  ".ai/INTERVIEW_AGENT.md" ".ai/OPERATOR_QUICKCARD.md" ".ai/FILE_MAP.md" ".ai/DIAGRAMS.md" "LEARNING_METHOD.md"
  ".interviewer/FAILURE_LOG.md" ".interviewer/PROGRESS.md" ".interviewer/MASTERY_MATRIX.md"
  ".interviewer/REPO_INDEX.md" ".interviewer/CANDIDATE_MODEL.md" ".interviewer/INTERVIEWER_STATE.md"
  ".interviewer/SESSION_HISTORY.md" ".interviewer/EVIDENCE_LOG.md" ".interviewer/SELF_MODEL.md"
  ".interviewer/TOPIC_COVERAGE.md" ".interviewer/GAMIFICATION.md" ".interviewer/PARAMETERS.json"
  "review/review.json" "config/repositories.json" "cache/build_cache.py" "tools/seal.py"
)
for relative in "${required[@]}"; do
  [[ -f "$practice_root/$relative" ]] || { echo "Missing required file: $relative" >&2; exit 1; }
done
if find "$practice_root" -mindepth 1 -maxdepth 1 -type d -name '20[0-9][0-9]' -print -quit | grep -q .; then
  echo "Legacy year-based session directory found; sessions must be flat under sessions/." >&2
  exit 1
fi
if [[ -d "$practice_root/sessions" ]]; then
  while IFS= read -r directory; do
    name="$(basename -- "$directory")"
    [[ "$name" =~ ^session-[0-9]{4}-[0-9]{4}-[0-9]{2}-[0-9]{2}$ ]] || { echo "Invalid flat session name: $name" >&2; exit 1; }
  done < <(find "$practice_root/sessions" -mindepth 1 -maxdepth 1 -type d -print)
fi
bash "$script_dir/rebuild-profile.sh"
bash "$script_dir/review-os.sh" stats
echo "MocksPractice validation passed."
