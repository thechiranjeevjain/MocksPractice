# Round 3 Editorial — Atomic Per-Key Sequence Gate

## TRIGGER

Check-and-update must be atomic for each key while independent keys should progress concurrently.

## PATTERN

`ConcurrentHashMap.compute` for per-key atomic mutation.

## INVARIANT

Stored value is the maximum accepted sequence for that stream and never decreases.

## TEMPLATE

Validate -> `compute(key, (k, old) -> condition ? newValue : old)` -> capture decision.

## FALLBACK

One `synchronized` method is correct but serializes unrelated streams and creates a global bottleneck.

## OPTIMIZATION

Use map-provided per-key atomicity; avoid a separate check followed by `put`.

## Final implementation / answer

```java
static final class SequenceGate {
    private final ConcurrentHashMap<String, Long> lastByStream = new ConcurrentHashMap<>();

    boolean accept(String streamId, long sequence) {
        validate(streamId);
        if (sequence < 0) throw new IllegalArgumentException("sequence must be non-negative");
        var accepted = new java.util.concurrent.atomic.AtomicBoolean();
        lastByStream.compute(streamId, (key, previous) -> {
            if (previous == null || sequence > previous) {
                accepted.set(true);
                return sequence;
            }
            return previous;
        });
        return accepted.get();
    }

    long lastAccepted(String streamId) {
        validate(streamId);
        return lastByStream.getOrDefault(streamId, -1L);
    }

    private static void validate(String streamId) {
        if (streamId == null || streamId.isBlank())
            throw new IllegalArgumentException("streamId required");
    }
}
```

## Complexity

Expected O(1) time per operation and O(s) space for s streams.

## Edge cases

Unseen stream, zero sequence, duplicate concurrent sequence, stale sequence, and invalid identifiers.

## Observed candidate mistakes

None in this sample. Common errors are `get` then `put`, global locking, and accepting equal sequences.

## Next variation

Allow one bounded out-of-order window while still rejecting duplicates.
