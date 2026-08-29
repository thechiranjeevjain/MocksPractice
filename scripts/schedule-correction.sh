#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 3 || $# -gt 6 ]]; then
  echo "Usage: schedule-correction.sh <id> <title> <area> [again|hard|good|easy] [EASY|MEDIUM|HARD] [session-path]" >&2
  exit 2
fi
id="$1"; title="$2"; area="$3"; rating="${4:-again}"; difficulty="${5:-MEDIUM}"; session_path="${6:-}"
case "$rating" in again|hard|good|easy) ;; *) echo "Invalid rating: $rating" >&2; exit 2;; esac

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
practice_root="$(cd -- "$script_dir/.." && pwd)"
review_root="${REVIEW_OS_ROOT:-$(cd -- "$practice_root/../review-os" 2>/dev/null && pwd || true)}"
[[ -d "$review_root" ]] || { echo "Review OS not found. Set REVIEW_OS_ROOT." >&2; exit 2; }
jar="$review_root/target/review-os-1.0.0.jar"
if [[ ! -f "$jar" ]]; then
  if [[ -x "$review_root/mvnw" ]]; then "$review_root/mvnw" -q -DskipTests package; else sh "$review_root/mvnw" -q -DskipTests package; fi
fi
normalized="$(java -cp "$jar" "$practice_root/tools/AddCorrection.java" "$practice_root" "$id" "$title" "$area" "$difficulty" "$session_path" | tail -n 1)"
exec bash "$review_root/scripts/review-repo.sh" "$practice_root" done "$normalized" "$rating" \
  --solve-time 0 --hints 0 --compile-success true --no-sync
