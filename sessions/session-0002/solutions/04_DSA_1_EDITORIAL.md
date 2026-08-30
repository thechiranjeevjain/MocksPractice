# Round 4 Editorial — First and Last Occurrence

## TRIGGER

Sorted data asks for first/last, leftmost/rightmost, range, or insertion boundary.

## PATTERN

Lower bound for first index `>= target`; upper bound for first index `> target`.

## INVARIANT

The answer boundary remains inside the half-open search interval `[low, high)`.

## TEMPLATE

`while (low < high) { mid = low + (high-low)/2; if (a[mid] < threshold) low=mid+1; else high=mid; }`

## FALLBACK

Linear scan is O(n), correct but too slow when repeated queries should exploit sorted order.

## OPTIMIZATION

Use two boundary searches rather than finding one match and scanning outward.

## Final implementation / answer

```java
static int[] searchRange(int[] values, int target) {
    int first = bound(values, target, false);
    if (first == values.length || values[first] != target) return new int[]{-1, -1};
    return new int[]{first, bound(values, target, true) - 1};
}

private static int bound(int[] values, int target, boolean upper) {
    int low = 0, high = values.length;
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (values[mid] < target || (upper && values[mid] == target)) low = mid + 1;
        else high = mid;
    }
    return low;
}
```

## Complexity

O(log n) time and O(1) auxiliary space.

## Edge cases

Empty array, absent target, all duplicates, target at either end, and single element.

## Observed candidate mistakes

None in this sample. Typical misses are closed/half-open interval mixing and `last = upperBound` without subtracting one.

## Next variation

Return only the count of target occurrences.
