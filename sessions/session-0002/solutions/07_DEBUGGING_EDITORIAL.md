# Round 7 Editorial — Identity, Overflow, and Aggregation

## TRIGGER

Plausible collection code fails around object equality, numeric width, duplicate records, and output identity.

## PATTERN

Restate the contract -> build adversarial examples -> fix the smallest correctness boundary -> preserve deterministic output.

## INVARIANT

Every fill for the requested desk contributes `quantity * priceCents` exactly once using `long` arithmetic.

## TEMPLATE

Validate -> value-equality filter -> `multiplyExact` -> `addExact` -> emit requested desk only when matched.

## FALLBACK

Patch only the first failing assertion. It may make one example pass but leaves overflow and duplicate-loss bugs.

## OPTIMIZATION

The requested desk implies at most one result, so aggregate directly rather than building a map keyed by order id.

## Final implementation / answer

```java
static List<DeskTotal> summarize(List<Fill> fills, String requestedDesk) {
    if (fills == null || requestedDesk == null || requestedDesk.isBlank())
        throw new IllegalArgumentException("fills and requestedDesk required");

    long total = 0;
    boolean matched = false;
    for (Fill fill : fills) {
        if (fill == null) throw new IllegalArgumentException("null fill");
        if (requestedDesk.equals(fill.desk())) {
            total = Math.addExact(total, Math.multiplyExact(fill.quantity(), fill.priceCents()));
            matched = true;
        }
    }
    return matched ? List.of(new DeskTotal(requestedDesk, total)) : List.of();
}
```

The original used `==` for strings, narrowed notional to `int`, overwrote repeated order ids, and mislabeled order ids as desks. Its comparator also lacked a tie-break, though the repaired contract returns at most one desk.

## Complexity

O(n) time and O(1) auxiliary space beyond the result.

## Edge cases

Equivalent non-interned strings, large multiplication, duplicate order ids, empty/no-match input, and null boundaries.

## Observed candidate mistakes

None in this sample. The deliberate bugs are the diagnostic targets listed above.

## Next variation

Remove `requestedDesk` and return totals for all desks with the specified deterministic ordering.
