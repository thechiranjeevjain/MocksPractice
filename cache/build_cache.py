#!/usr/bin/env python3
"""Incremental SQLite/FTS5 corpus cache for bounded mock-interview planning."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import sqlite3
import subprocess
import sys
import zipfile
from datetime import date, datetime, timezone
from pathlib import Path
from typing import Iterable
from xml.etree import ElementTree

PRACTICE_ROOT = Path(__file__).resolve().parents[1]
CONFIG_PATH = PRACTICE_ROOT / "config" / "repositories.json"
DB_PATH = PRACTICE_ROOT / "cache" / "interview_cache.sqlite3"
EXCLUDED = {".git", ".idea", "target", "build", "out", "node_modules", ".m2repo", "_archive", "coverage", "dist", ".venv", "__pycache__"}


def now() -> str:
    return datetime.now(timezone.utc).isoformat()


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def run_git(root: Path, *args: str) -> str | None:
    try:
        result = subprocess.run(["git", "-C", str(root), *args], capture_output=True, text=True, check=True)
        return result.stdout.strip()
    except (OSError, subprocess.CalledProcessError):
        return None


def git_state(root: Path) -> tuple[str, bool]:
    top = run_git(root, "rev-parse", "--show-toplevel")
    if not top:
        return "NO_GIT", False
    head = run_git(root, "rev-parse", "HEAD") or "NO_HEAD"
    status = run_git(root, "status", "--porcelain")
    return head, status == ""


def resolve_root(config: dict[str, object]) -> Path | None:
    import os

    candidates: list[Path] = []
    environment = os.environ.get(str(config.get("environmentVariable", "")), "")
    if environment:
        candidates.append(Path(environment))
    relative = str(config.get("relativeToPracticeParent", ""))
    if relative:
        candidates.append(PRACTICE_ROOT.parent / relative)
    fallback = str(config.get("windowsFallback", ""))
    if fallback:
        candidates.append(Path(fallback))
    for candidate in candidates:
        resolved = candidate.expanduser().resolve()
        if resolved.is_dir():
            return resolved
    return None


def completed_session_names() -> set[str]:
    history = PRACTICE_ROOT / ".interviewer" / "SESSION_HISTORY.md"
    if not history.exists():
        return set()
    names: set[str] = set()
    for line in history.read_text(encoding="utf-8").splitlines():
        if "COMPLETED" not in line:
            continue
        match = re.search(r"sessions/(session-\d{4})", line)
        if match:
            names.add(match.group(1))
    return names


def iter_files(root: Path, extensions: set[str], completed_only: bool) -> Iterable[Path]:
    completed = completed_session_names() if completed_only else set()
    for path in root.rglob("*"):
        if not path.is_file() or path.suffix.lower() not in extensions:
            continue
        relative = path.relative_to(root)
        if any(part.lower() in EXCLUDED for part in relative.parts):
            continue
        if completed_only and (not relative.parts or relative.parts[0] not in completed):
            continue
        yield path


def extract_docx(path: Path) -> str:
    with zipfile.ZipFile(path) as archive:
        document = ElementTree.fromstring(archive.read("word/document.xml"))
    return " ".join(node.text or "" for node in document.iter() if node.tag.endswith("}t"))


def extract_pdf(path: Path) -> str:
    try:
        from pypdf import PdfReader
    except ImportError as error:
        raise RuntimeError("Install cache/requirements.txt to extract PDF files") from error
    return "\n".join(page.extract_text() or "" for page in PdfReader(str(path)).pages)


def extract_text(path: Path) -> str:
    suffix = path.suffix.lower()
    if suffix == ".pdf":
        return extract_pdf(path)
    if suffix == ".docx":
        return extract_docx(path)
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except OSError as error:
        return f"[EXTRACTION ERROR: {error}]"


def schema(connection: sqlite3.Connection) -> None:
    connection.executescript("""
        PRAGMA journal_mode=WAL;
        CREATE TABLE IF NOT EXISTS sources(
            name TEXT PRIMARY KEY, root TEXT NOT NULL, available INTEGER NOT NULL,
            git_head TEXT, git_clean INTEGER NOT NULL DEFAULT 0, scanned_at TEXT NOT NULL
        );
        CREATE TABLE IF NOT EXISTS files(
            id INTEGER PRIMARY KEY, source TEXT NOT NULL, rel_path TEXT NOT NULL,
            abs_path TEXT NOT NULL, sha256 TEXT NOT NULL, size INTEGER NOT NULL,
            mtime_ns INTEGER NOT NULL, extension TEXT NOT NULL, extracted_text TEXT NOT NULL,
            updated_at TEXT NOT NULL, UNIQUE(source, rel_path)
        );
        CREATE VIRTUAL TABLE IF NOT EXISTS files_fts USING fts5(source UNINDEXED, rel_path UNINDEXED, content);
        CREATE TABLE IF NOT EXISTS catalog(
            source TEXT NOT NULL, rel_path TEXT NOT NULL, priority TEXT NOT NULL,
            interview_value REAL NOT NULL, pattern TEXT, tags TEXT,
            PRIMARY KEY(source, rel_path)
        );
        CREATE TABLE IF NOT EXISTS cards(
            id TEXT PRIMARY KEY, source TEXT NOT NULL, title TEXT NOT NULL, priority TEXT NOT NULL,
            interview_value REAL NOT NULL, trigger_text TEXT, pattern_text TEXT,
            invariant_text TEXT, template_text TEXT, fallback_text TEXT, optimization_text TEXT
        );
        CREATE TABLE IF NOT EXISTS nodes(id TEXT PRIMARY KEY, kind TEXT NOT NULL, label TEXT NOT NULL, source TEXT, rel_path TEXT);
        CREATE TABLE IF NOT EXISTS edges(source_id TEXT NOT NULL, target_id TEXT NOT NULL, relation TEXT NOT NULL,
            PRIMARY KEY(source_id, target_id, relation));
        CREATE TABLE IF NOT EXISTS meta(key TEXT PRIMARY KEY, value TEXT NOT NULL);
    """)


def priority(problem: dict[str, object]) -> tuple[str, float]:
    due = str(problem.get("nextReview") or "")
    failures = int(problem.get("compileFailures") or 0)
    hints = int(problem.get("hintUsedCount") or 0)
    repetitions = int(problem.get("repetitions") or 0)
    if failures or hints or (due and due <= date.today().isoformat()):
        return "A", 3.0
    if repetitions < 2:
        return "B", 2.0
    return "C", 1.0


def review_records(root: Path) -> list[dict[str, object]]:
    review = root / "review" / "review.json"
    if not review.exists():
        return []
    try:
        document = json.loads(review.read_text(encoding="utf-8"))
        return [item for item in document.get("problems", []) if isinstance(item, dict)]
    except (OSError, json.JSONDecodeError):
        return []


def six_parts(problem: dict[str, object]) -> dict[str, str]:
    text = "\n".join(str(problem.get(key) or "") for key in ("prompt", "answer"))
    result = {key: "" for key in ("trigger", "pattern", "invariant", "template", "fallback", "optimization")}
    for key in result:
        match = re.search(rf"(?im)^\s*{key}\s*[:\-]\s*(.+)$", text)
        if match:
            result[key] = match.group(1).strip()
    result["pattern"] = result["pattern"] or str(problem.get("pattern") or "")
    return result


def rebuild_catalog(connection: sqlite3.Connection, source: str, root: Path) -> None:
    problems = review_records(root)
    path_metadata: dict[str, tuple[str, float, str, str]] = {}
    connection.execute("DELETE FROM cards WHERE source = ?", (source,))
    for problem in problems:
        grade, value = priority(problem)
        pattern = str(problem.get("pattern") or "")
        tags = json.dumps(problem.get("tags") or [])
        for field in ("codePath", "notesPath"):
            value_path = str(problem.get(field) or "").replace("\\", "/").lstrip("./").lower()
            if value_path:
                path_metadata[value_path] = (grade, value, pattern, tags)
        for ref in problem.get("sourceRefs") or []:
            value_path = str(ref).replace("\\", "/").lstrip("./").lower()
            if value_path:
                path_metadata[value_path] = (grade, value, pattern, tags)
        parts = six_parts(problem)
        card_id = f"{source}:{problem.get('id', problem.get('title', 'unknown'))}"
        connection.execute("""INSERT OR REPLACE INTO cards VALUES(?,?,?,?,?,?,?,?,?,?,?)""", (
            card_id, source, str(problem.get("title") or card_id), grade, value,
            parts["trigger"], parts["pattern"], parts["invariant"], parts["template"], parts["fallback"], parts["optimization"]
        ))
    connection.execute("DELETE FROM catalog WHERE source = ?", (source,))
    rows = connection.execute("SELECT rel_path FROM files WHERE source = ?", (source,)).fetchall()
    for (relative,) in rows:
        normalized = relative.lower()
        metadata = next((item for path, item in path_metadata.items() if normalized.endswith(path) or path.endswith(normalized)), None)
        grade, value, pattern, tags = metadata or ("C", 1.0, "", "[]")
        connection.execute("INSERT INTO catalog VALUES(?,?,?,?,?,?)", (source, relative, grade, value, pattern, tags))


def rebuild_graph(connection: sqlite3.Connection, source: str) -> None:
    file_ids = [row[0] for row in connection.execute("SELECT id FROM nodes WHERE source = ?", (source,))]
    for node_id in file_ids:
        connection.execute("DELETE FROM edges WHERE source_id = ? OR target_id = ?", (node_id, node_id))
    connection.execute("DELETE FROM nodes WHERE source = ?", (source,))
    source_id = f"source:{source}"
    connection.execute("INSERT OR REPLACE INTO nodes VALUES(?,?,?,?,?)", (source_id, "source", source, source, ""))
    for relative, pattern in connection.execute("SELECT rel_path, pattern FROM catalog WHERE source = ?", (source,)):
        file_id = f"file:{source}:{relative}"
        connection.execute("INSERT OR REPLACE INTO nodes VALUES(?,?,?,?,?)", (file_id, "file", Path(relative).name, source, relative))
        connection.execute("INSERT OR REPLACE INTO edges VALUES(?,?,?)", (source_id, file_id, "contains"))
        if pattern:
            pattern_id = f"pattern:{pattern.lower()}"
            connection.execute("INSERT OR REPLACE INTO nodes VALUES(?,?,?,?,?)", (pattern_id, "pattern", pattern, "", ""))
            connection.execute("INSERT OR REPLACE INTO edges VALUES(?,?,?)", (file_id, pattern_id, "covers"))


def build(full: bool) -> None:
    config = json.loads(CONFIG_PATH.read_text(encoding="utf-8"))
    defaults = {value.lower() for value in config["defaultExtensions"]}
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    if full and DB_PATH.exists():
        archive_root = PRACTICE_ROOT / ".interviewer" / "archive" / "cache"
        archive_root.mkdir(parents=True, exist_ok=True)
        stamp = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ")
        archive_path = archive_root / f"interview_cache-{stamp}.sqlite3"
        with sqlite3.connect(DB_PATH) as source_db, sqlite3.connect(archive_path) as archive_db:
            source_db.backup(archive_db)
        print(f"Archived previous cache: {archive_path}")
    with sqlite3.connect(DB_PATH) as connection:
        schema(connection)
        for item in config["repositories"]:
            source = item["name"]
            root = resolve_root(item)
            if root is None:
                if item.get("required", True):
                    raise SystemExit(f"Required source unavailable: {source}")
                connection.execute("INSERT OR REPLACE INTO sources VALUES(?,?,?,?,?,?)", (source, "UNAVAILABLE", 0, "", 0, now()))
                print(f"{source}: unavailable (optional)")
                continue
            head, clean = git_state(root)
            previous = connection.execute("SELECT git_head, git_clean FROM sources WHERE name = ?", (source,)).fetchone()
            if not full and clean and previous and previous[0] == head and previous[1] == 1:
                print(f"{source}: gated by unchanged clean HEAD {head[:12]}")
                continue
            extension_csv = str(item.get("extensionsCsv") or "")
            extensions = {value.strip().lower() for value in extension_csv.split(",") if value.strip()} or defaults
            seen: set[str] = set()
            changed = 0
            for path in iter_files(root, extensions, bool(item.get("completedSessionsOnly", False))):
                relative = path.relative_to(root).as_posix()
                seen.add(relative)
                digest = sha256(path)
                existing = connection.execute("SELECT sha256 FROM files WHERE source = ? AND rel_path = ?", (source, relative)).fetchone()
                if existing and existing[0] == digest:
                    continue
                text = extract_text(path)
                stat = path.stat()
                connection.execute("""INSERT INTO files(source,rel_path,abs_path,sha256,size,mtime_ns,extension,extracted_text,updated_at)
                    VALUES(?,?,?,?,?,?,?,?,?) ON CONFLICT(source,rel_path) DO UPDATE SET abs_path=excluded.abs_path,
                    sha256=excluded.sha256,size=excluded.size,mtime_ns=excluded.mtime_ns,extension=excluded.extension,
                    extracted_text=excluded.extracted_text,updated_at=excluded.updated_at""",
                    (source, relative, str(path), digest, stat.st_size, stat.st_mtime_ns, path.suffix.lower(), text, now()))
                connection.execute("DELETE FROM files_fts WHERE source = ? AND rel_path = ?", (source, relative))
                connection.execute("INSERT INTO files_fts VALUES(?,?,?)", (source, relative, text))
                changed += 1
            existing_paths = {row[0] for row in connection.execute("SELECT rel_path FROM files WHERE source = ?", (source,))}
            for missing in existing_paths - seen:
                connection.execute("DELETE FROM files WHERE source = ? AND rel_path = ?", (source, missing))
                connection.execute("DELETE FROM files_fts WHERE source = ? AND rel_path = ?", (source, missing))
            connection.execute("INSERT OR REPLACE INTO sources VALUES(?,?,?,?,?,?)", (source, str(root), 1, head, int(clean), now()))
            rebuild_catalog(connection, source, root)
            rebuild_graph(connection, source)
            print(f"{source}: indexed={len(seen)} changed={changed} removed={len(existing_paths - seen)}")
        connection.execute("INSERT OR REPLACE INTO meta VALUES('lastBuildUtc', ?)", (now(),))
        connection.commit()
    print(f"Cache: {DB_PATH}")


def search(query: str, limit: int) -> None:
    with sqlite3.connect(DB_PATH) as connection:
        rows = connection.execute("""SELECT f.source,f.rel_path,c.priority,c.interview_value,
            snippet(files_fts,2,'[',']','...',24) FROM files_fts f
            LEFT JOIN catalog c ON c.source=f.source AND c.rel_path=f.rel_path
            WHERE files_fts MATCH ? ORDER BY bm25(files_fts), c.interview_value DESC LIMIT ?""", (query, limit)).fetchall()
    print(json.dumps([{"source": a, "path": b, "priority": c, "interviewValue": d, "snippet": e} for a, b, c, d, e in rows], indent=2))


def catalog(grade: str | None, limit: int) -> None:
    sql = "SELECT source,rel_path,priority,interview_value,pattern,tags FROM catalog"
    params: list[object] = []
    if grade:
        sql += " WHERE priority = ?"
        params.append(grade)
    sql += " ORDER BY interview_value DESC, source, rel_path LIMIT ?"
    params.append(limit)
    with sqlite3.connect(DB_PATH) as connection:
        rows = connection.execute(sql, params).fetchall()
    print(json.dumps([{"source": a, "path": b, "priority": c, "interviewValue": d, "pattern": e, "tags": json.loads(f)} for a, b, c, d, e, f in rows], indent=2))


def export_graph() -> None:
    destination = PRACTICE_ROOT / "cache" / "exports" / "graph.json"
    destination.parent.mkdir(parents=True, exist_ok=True)
    with sqlite3.connect(DB_PATH) as connection:
        nodes = [dict(zip(("id", "kind", "label", "source", "path"), row)) for row in connection.execute("SELECT * FROM nodes")]
        edges = [dict(zip(("source", "target", "relation"), row)) for row in connection.execute("SELECT * FROM edges")]
    destination.write_text(json.dumps({"nodes": nodes, "edges": edges}, indent=2) + "\n", encoding="utf-8")
    print(destination)


def sync_sources(dry_run: bool) -> None:
    config = json.loads(CONFIG_PATH.read_text(encoding="utf-8"))
    outcomes: list[str] = []
    for item in config["repositories"]:
        source = item["name"]
        if source == "MOCK_HISTORY":
            outcomes.append(f"{source}=SKIPPED_SELF")
            continue
        root = resolve_root(item)
        if root is None:
            outcomes.append(f"{source}=UNAVAILABLE")
            continue
        top = run_git(root, "rev-parse", "--show-toplevel")
        if not top:
            outcomes.append(f"{source}=SKIPPED_NOT_GIT")
            continue
        if Path(top).resolve() != root.resolve():
            outcomes.append(f"{source}=SKIPPED_SHARED_GIT_ROOT")
            continue
        if dry_run:
            outcomes.append(f"{source}=DRY_RUN_WOULD_PULL_FF_ONLY")
            continue
        try:
            result = subprocess.run(["git", "-C", str(root), "pull", "--ff-only"], capture_output=True, text=True, check=False)
            summary = (result.stdout.strip() or result.stderr.strip() or f"exit {result.returncode}").replace("\n", " | ")
            outcomes.append(f"{source}={'OK' if result.returncode == 0 else 'NON_FATAL_FAILURE'}:{summary[:300]}")
        except OSError as error:
            outcomes.append(f"{source}=NON_FATAL_FAILURE:{error}")
    log = PRACTICE_ROOT / ".interviewer" / "SOURCE_SYNC_LOG.md"
    with log.open("a", encoding="utf-8") as stream:
        stream.write(f"\n- {now()} | " + " ; ".join(outcomes) + "\n")
    print("\n".join(outcomes))


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="command", required=True)
    build_parser = sub.add_parser("build")
    build_parser.add_argument("--full", action="store_true")
    search_parser = sub.add_parser("search")
    search_parser.add_argument("query")
    search_parser.add_argument("--limit", type=int, default=12)
    catalog_parser = sub.add_parser("catalog")
    catalog_parser.add_argument("--priority", choices=("A", "B", "C"))
    catalog_parser.add_argument("--limit", type=int, default=20)
    sub.add_parser("export-graph")
    sync_parser = sub.add_parser("sync")
    sync_parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    limit = min(max(getattr(args, "limit", 1), 1), 50)
    if args.command == "build":
        build(args.full)
    elif args.command == "search":
        search(args.query, limit)
    elif args.command == "catalog":
        catalog(args.priority, limit)
    elif args.command == "export-graph":
        export_graph()
    else:
        sync_sources(args.dry_run)


if __name__ == "__main__":
    main()
