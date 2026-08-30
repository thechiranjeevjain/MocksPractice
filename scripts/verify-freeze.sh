#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: verify-freeze.sh <session-path>" >&2
  exit 2
fi
script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
practice_root="$(cd -- "$script_dir/.." && pwd)"
exec java "$practice_root/tools/FreezeSession.java" "$practice_root" "$1" --verify
