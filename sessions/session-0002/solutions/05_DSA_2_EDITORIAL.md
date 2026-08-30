# Round 5 Editorial — Minimum Shipping Capacity

## TRIGGER

Find the minimum feasible numeric answer under a monotonic predicate.

## PATTERN

Binary search on answer: capacity from maximum single weight to total weight.

## INVARIANT

Capacities below the answer are infeasible; the current inclusive range still contains the minimum feasible capacity.

## TEMPLATE

Choose bounds -> implement O(n) feasibility -> shrink toward first feasible answer.

## FALLBACK

Try every capacity from max to sum and simulate days: O(n * answer-range), too slow when the range is large.

## OPTIMIZATION

Binary-search the monotonic capacity space, making total work O(n log sum).

## Final implementation / answer

```java
static int minimumCapacity(int[] weights, int days) {
    int low = 0, high = 0;
    for (int weight : weights) {
        low = Math.max(low, weight);
        high = Math.addExact(high, weight);
    }
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (fits(weights, days, mid)) high = mid;
        else low = mid + 1;
    }
    return low;
}

private static boolean fits(int[] weights, int allowedDays, int capacity) {
    int days = 1, load = 0;
    for (int weight : weights) {
        if (load + weight > capacity) {
            days++;
            load = 0;
        }
        load += weight;
        if (days > allowedDays) return false;
    }
    return true;
}
```

## Complexity

O(n log(sum(weights) - max(weights) + 1)) time and O(1) space.

## Edge cases

One day, one package per day, singleton input, dominant heavy package, and overflow of total weight.

## Observed candidate mistakes

None in this sample. Common errors are starting low at zero and reordering packages.

## Next variation

Return the minimum largest partition sum for exactly `m` non-empty partitions.
