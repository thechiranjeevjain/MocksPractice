# Local Interview Corpus Cache

The cache keeps mock planning bounded. It crawls configured read-only sources, hashes files, extracts text, builds SQLite FTS5, derives an A/B/C catalog from Review OS, stores distilled cards, and builds a small relationship graph.

## Setup

Windows:

```powershell
py -3 -m venv cache\.venv
.\cache\.venv\Scripts\python.exe -m pip install -r cache\requirements.txt
.\cache\.venv\Scripts\python.exe cache\build_cache.py build
```

Linux/macOS:

```bash
python3 -m venv cache/.venv
cache/.venv/bin/python -m pip install -r cache/requirements.txt
cache/.venv/bin/python cache/build_cache.py build
```

Use `build --full` on Monday/every seventh session. Normal builds skip clean Git sources whose HEAD is unchanged and still invalidate individual files by SHA-256 when a source is dirty or non-Git.

## Bounded retrieval

```bash
python cache/build_cache.py search "spring transaction propagation" --limit 12
python cache/build_cache.py catalog --priority A --limit 20
python cache/build_cache.py export-graph
```

Never dump the whole database into prompt context. Retrieve only the rows needed for PLAN.
