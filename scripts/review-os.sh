#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
practice_root="$(cd -- "$script_dir/.." && pwd)"
review_root="${REVIEW_OS_ROOT:-$(cd -- "$practice_root/../review-os" 2>/dev/null && pwd || true)}"
if [[ -z "$review_root" || ! -d "$review_root" ]]; then
  echo "Review OS not found. Keep review-os beside MocksPractice or set REVIEW_OS_ROOT." >&2
  exit 2
fi
exec bash "$review_root/scripts/review-repo.sh" "$practice_root" "$@"
