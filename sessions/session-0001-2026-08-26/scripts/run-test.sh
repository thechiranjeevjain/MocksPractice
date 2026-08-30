#!/usr/bin/env bash
set -uo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: run-test.sh <test-class> <round>" >&2
  exit 2
fi
test_class="$1"; round="$2"
script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
session_root="$(cd -- "$script_dir/.." && pwd)"
practice_root="$session_root"
while [[ "$practice_root" != "/" && ! -f "$practice_root/.ai/INTERVIEW_AGENT.md" ]]; do
  practice_root="$(dirname -- "$practice_root")"
done
[[ -f "$practice_root/.ai/INTERVIEW_AGENT.md" ]] || { echo "MocksPractice root was not found above the session directory." >&2; exit 2; }
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

tests=0; failures=0; errors=0
report_dir="$session_root/target/surefire-reports"
if [[ -d "$report_dir" ]]; then
  while IFS= read -r line; do
    current_tests="$(printf '%s' "$line" | sed -n 's/.* tests="\([0-9][0-9]*\)".*/\1/p')"
    current_failures="$(printf '%s' "$line" | sed -n 's/.* failures="\([0-9][0-9]*\)".*/\1/p')"
    current_errors="$(printf '%s' "$line" | sed -n 's/.* errors="\([0-9][0-9]*\)".*/\1/p')"
    tests=$((tests + ${current_tests:-0})); failures=$((failures + ${current_failures:-0})); errors=$((errors + ${current_errors:-0}))
  done < <(grep -h -m1 '<testsuite ' "$report_dir"/TEST-*.xml 2>/dev/null || true)
fi
timestamp="$(date '+%Y-%m-%d %H:%M:%S %z')"
printf '\n- %s | %s | exit=%s | tests=%s failures=%s errors=%s\n' \
  "$timestamp" "$round" "$exit_code" "$tests" "$failures" "$errors" >> "$practice_root/.interviewer/EVIDENCE_LOG.md"
exit "$exit_code"
