# Round 2 — Core Java, Streams, and Functional APIs

You receive:

```java
record Trade(String id, String desk, long quantity, long priceCents, Status status) {}
enum Status { NEW, FILLED, CANCELLED }
```

From memory, write a side-effect-free Java method that:

1. ignores non-`FILLED` trades;
2. computes total notional (`quantity * priceCents`) per desk using `long` arithmetic;
3. returns the top two desks by notional descending, then desk name ascending;
4. does not mutate the input; and
5. defines sensible behavior for null input, null elements, and blank desk names.

Before code, write the six-part generator. Then explain:

- `map` versus `flatMap`;
- why stateful mutation inside `map`/`forEach` is risky;
- when `toMap` needs a merge function;
- whether the pipeline is safe to switch to `parallelStream()`; and
- when a loop is clearer than a stream.
