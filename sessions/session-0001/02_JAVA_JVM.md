# Round 2 — Applied Java and JVM

Target: 12 minutes

## Question 1

Consider this code:

```java
record Key(String account, java.util.List<String> venues) {}

var venues = new java.util.ArrayList<>(java.util.List.of("XNYS"));
var key = new Key("A-7", venues);
var limits = new java.util.HashMap<Key, Long>();
limits.put(key, 100L);
venues.add("XNAS");
```

Explain precisely what contract may be violated, what observable symptom can follow, and how you would redesign the API boundary.

### Candidate answer

TODO

## Question 2

A latency-sensitive Java service has stable average latency but p99.9 doubles during bursts. CPU averages 45%, no exception rate changed, and allocation rate increased after a release. Give a causal investigation order. State what JFR, GC logs, and an allocation profiler can and cannot prove.

### Candidate answer

TODO

## Question 3

An engineer proposes moving every hot-path object off heap to eliminate GC. Give the strongest argument for and against the proposal, including ownership/lifetime, copies, memory safety, observability, and how you would benchmark the decision.

### Candidate answer

TODO
