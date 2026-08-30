# Operating Diagrams

## Mock lifecycle

```mermaid
flowchart LR
    A[START MOCK] --> B[Pull ff-only]
    B --> C[Gated cache build]
    C --> D[Source and state reconciliation]
    D --> E[Allocate global NNNN]
    E --> F[PLAN]
    F --> G[BUILD complete paper]
    G --> H[LOCK solutions and seal refs]
    H --> I[Freeze and attempt]
    I --> J[DONE audit and tests]
    J --> K[Viva and variation]
    K --> L[Score and editorials]
    L --> M[FSRS plus state and digest]
```

## Mastery lifecycle

```mermaid
stateDiagram-v2
    [*] --> NOT_SOLID: first failed evidence
    NOT_SOLID --> PROVISIONAL: one clean spaced reconstruction
    PROVISIONAL --> SOLID: second clean spaced reconstruction
    PROVISIONAL --> NOT_SOLID: retrieval or invariant failure
    SOLID --> NOT_SOLID: regression
    SOLID --> SOLID: harder combination succeeds
```

`PROVISIONAL` is a lifecycle state only; reported mastery remains binary `NOT-SOLID` until the second clean reconstruction.

## Bounded retrieval

```mermaid
flowchart TD
    R[Living read-only sources] --> H[Git HEAD and SHA-256 gate]
    H --> X[Text extraction]
    X --> F[SQLite FTS5]
    F --> C[Priority catalog and cards]
    C --> G[Graph nodes and edges]
    F --> Q[Bounded search]
    C --> Q
    G --> Q
    Q --> P[Examiner PLAN]
```
