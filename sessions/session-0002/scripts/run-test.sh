#!/usr/bin/env bash
set -uo pipefail

if [[ $# -ne 2 ]]; then echo "Usage: run-test.sh <test-class> <round>" >&2; exit 2; fi
test_class="$1"; round="$2"
script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
session_root="$(cd -- "$script_dir/.." && pwd)"
practice_root="$session_root"
while [[ "$practice_root" != "/" && ! -f "$practice_root/.ai/INTERVIEW_AGENT.md" ]]; do practice_root="$(dirname -- "$practice_root")"; done
[[ -f "$practice_root/.ai/INTERVIEW_AGENT.md" ]] || { echo "MocksPractice root was not found." >&2; exit 2; }
review_root="${REVIEW_OS_ROOT:-$(cd -- "$practice_root/../review-os" 2>/dev/null && pwd || true)}"
[[ -d "$review_root" ]] || { echo "Review OS not found. Set REVIEW_OS_ROOT." >&2; exit 2; }

set +e
if [[ -x "$review_root/mvnw" ]]; then
  "$review_root/mvnw" -q -f "$session_root/pom.xml" "-Dtest=$test_class" test
else
  sh "$review_root/mvnw" -q -f "$session_root/pom.xml" "-Dtest=$test_class" test
fi
exit_code=$?
set -e
timestamp="$(date '+%Y-%m-%d %H:%M:%S %z')"
printf '\n- %s | SAMPLE-%s | exit=%s\n' "$timestamp" "$round" "$exit_code" >> "$practice_root/.interviewer/EVIDENCE_LOG.md"
exit "$exit_code"
