#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
practice_root="$(cd -- "$script_dir/.." && pwd)"
if [[ -x "$practice_root/cache/.venv/bin/python" ]]; then
  python_bin="$practice_root/cache/.venv/bin/python"
elif [[ -x "$practice_root/cache/.venv/Scripts/python.exe" ]]; then
  python_bin="$practice_root/cache/.venv/Scripts/python.exe"
elif command -v python3 >/dev/null 2>&1; then
  python_bin="$(command -v python3)"
else
  python_bin="$(command -v python)"
fi
exec "$python_bin" "$practice_root/cache/build_cache.py" build "$@"
