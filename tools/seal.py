#!/usr/bin/env python3
"""Reversible anti-peek obfuscation for examiner references.

This is deliberately described as obfuscation, not strong encryption. The key is
supplied through MOCK_SEAL_KEY and is never written to the repository.
"""

from __future__ import annotations

import argparse
import base64
import hashlib
import hmac
import json
import os
import secrets
from pathlib import Path


def key_bytes() -> bytes:
    value = os.environ.get("MOCK_SEAL_KEY", "")
    if not value:
        raise SystemExit("Set MOCK_SEAL_KEY before sealing or unsealing examiner references.")
    return hashlib.sha256(value.encode("utf-8")).digest()


def stream(key: bytes, nonce: bytes, size: int) -> bytes:
    output = bytearray()
    counter = 0
    while len(output) < size:
        output.extend(hashlib.sha256(key + nonce + counter.to_bytes(8, "big")).digest())
        counter += 1
    return bytes(output[:size])


def xor(left: bytes, right: bytes) -> bytes:
    return bytes(a ^ b for a, b in zip(left, right, strict=True))


def seal_bytes(plain: bytes, key: bytes) -> dict[str, str | int]:
    nonce = secrets.token_bytes(16)
    payload = xor(plain, stream(key, nonce, len(plain)))
    tag = hmac.new(key, nonce + payload, hashlib.sha256).digest()
    return {
        "schemaVersion": 1,
        "notice": "anti-peek obfuscation; not strong encryption",
        "nonce": base64.b64encode(nonce).decode("ascii"),
        "payload": base64.b64encode(payload).decode("ascii"),
        "hmac": base64.b64encode(tag).decode("ascii"),
    }


def unseal_bytes(document: dict[str, object], key: bytes) -> bytes:
    nonce = base64.b64decode(str(document["nonce"]), validate=True)
    payload = base64.b64decode(str(document["payload"]), validate=True)
    expected = base64.b64decode(str(document["hmac"]), validate=True)
    actual = hmac.new(key, nonce + payload, hashlib.sha256).digest()
    if not hmac.compare_digest(expected, actual):
        raise SystemExit("Seal integrity check failed. The key is wrong or the file changed.")
    return xor(payload, stream(key, nonce, len(payload)))


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="command", required=True)
    for name in ("seal", "unseal"):
        command = sub.add_parser(name)
        command.add_argument("source", type=Path)
        command.add_argument("destination", type=Path)
    args = parser.parse_args()
    key = key_bytes()
    args.destination.parent.mkdir(parents=True, exist_ok=True)
    if args.command == "seal":
        document = seal_bytes(args.source.read_bytes(), key)
        args.destination.write_text(json.dumps(document, indent=2) + "\n", encoding="utf-8")
    else:
        document = json.loads(args.source.read_text(encoding="utf-8"))
        args.destination.write_bytes(unseal_bytes(document, key))


if __name__ == "__main__":
    main()
