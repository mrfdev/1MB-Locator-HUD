# Domain docs

This repository uses a single-context domain-documentation layout.

## Before exploring

Read these when they exist:

- `CONTEXT.md` at the repository root.
- Relevant ADRs under `docs/adr/`.

If they do not exist, proceed silently. Do not create them preemptively. Domain-modeling workflows create them when terminology or architectural decisions genuinely need recording.

## File structure

```text
/
├── CONTEXT.md
├── docs/
│   └── adr/
│       ├── 0001-example-decision.md
│       └── 0002-another-decision.md
└── src/
```

## Use the glossary’s vocabulary

When an issue, specification, refactor, hypothesis, or test names a domain concept, use the terminology defined in `CONTEXT.md`.

If a necessary concept is absent, reconsider whether existing project terminology already covers it. Otherwise, record the gap for domain modeling.

## Flag ADR conflicts

If proposed work contradicts an existing ADR, identify the conflict explicitly rather than silently overriding the decision.
