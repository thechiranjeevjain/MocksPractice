# Round 6 Editorial — Version Predecessor Search

## TRIGGER

Sorted timestamps ask for the latest record at or before a query.

## PATTERN

Upper bound on timestamp, then step one position left.

## INVARIANT

Indices below `low` have timestamp `<= query`; indices at or above `high` have timestamp `> query`.

## TEMPLATE

Find first element `> query`; predecessor is index minus one.

## FALLBACK

Scan until timestamps exceed the query: O(n) per lookup.

## OPTIMIZATION

Use binary search for O(log n) lookup on immutable ordered history.

## Final implementation / answer

```java
static Optional<String> valueAt(List<Version> versions, long queryTimestamp) {
    int low = 0, high = versions.size();
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (versions.get(mid).timestamp() <= queryTimestamp) low = mid + 1;
        else high = mid;
    }
    return low == 0 ? Optional.empty() : Optional.of(versions.get(low - 1).value());
}
```

## Complexity

O(log n) time and O(1) auxiliary space.

## Edge cases

Empty history, before first, exact timestamp, between versions, after last, and negative timestamps.

## Observed candidate mistakes

None in this sample. Typical errors are returning the first greater value and mishandling `low == 0`.

## Next variation

Support many keys, each with its own ordered version history.
