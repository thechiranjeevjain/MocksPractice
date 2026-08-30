# Round 2 Editorial — Java Streams and Functional APIs

## TRIGGER

Filter records, aggregate by key, and select a deterministically ordered top K.

## PATTERN

Validate -> filter -> grouping collector -> entry stream -> comparator -> limit.

## INVARIANT

Each valid filled trade contributes its notional exactly once to exactly one desk, and ties have a total order.

## TEMPLATE

`stream().filter(...).collect(groupingBy(key, summingLong(value))).entrySet().stream().sorted(...).limit(k).toList()`

## FALLBACK

Use a loop and `Map.merge`; it is still O(n) and is often clearer. Sorting every trade before aggregation is wasteful and can be O(n log n).

## OPTIMIZATION

Aggregate first, then sort only the number of desks. For very many desks and tiny K, use a size-K heap.

## Final implementation / answer

```java
static List<Map.Entry<String, Long>> topTwo(List<Trade> trades) {
    if (trades == null || trades.stream().anyMatch(t ->
            t == null || t.desk() == null || t.desk().isBlank())) {
        throw new IllegalArgumentException("valid trades and desks required");
    }

    Map<String, Long> totals = trades.stream()
            .filter(t -> t.status() == Status.FILLED)
            .collect(Collectors.groupingBy(
                    Trade::desk,
                    Collectors.summingLong(t -> Math.multiplyExact(t.quantity(), t.priceCents()))));

    return totals.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                    .thenComparing(Map.Entry.comparingByKey()))
            .limit(2)
            .toList();
}
```

`map` performs one-to-one transformation; `flatMap` transforms one input to zero/many outputs and flattens them. External mutable state inside a pipeline breaks non-interference and becomes unsafe in parallel. `toMap` needs a merge function when keys can repeat. This pipeline's collectors are framework-managed, but `parallelStream()` is not automatically faster; measure data size and collector contention. Prefer a loop when it makes validation, overflow, or control flow clearer.

## Complexity

O(n + d log d) time and O(d) space, where d is the number of desks.

## Edge cases

Empty/no-filled input returns an empty list; duplicate desks merge; multiplication overflow fails loudly; ties use desk name.

## Observed candidate mistakes

None in this sample. Common misses: `toMap` without merging, `int` notional, and side effects in `forEach`.

## Next variation

Return top K desks for each region after adding exactly one `region` field.
