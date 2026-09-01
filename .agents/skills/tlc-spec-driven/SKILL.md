# tlc-spec-driven

## Purpose
Use specification-driven development for every Reclama product change. The spec is the source of truth; implementation is complete only when acceptance criteria are verified.

## Trigger
Apply this skill whenever work changes Reclama behavior, UX, data, architecture, integrations, build/release behavior, or fixes a bug.

## TLC workflow
TLC = Initialize → Think → Lock → Code → Verify.

### 0. Initialize
Before the first code change in a working session:
1. Read root `AGENTS.md` and this skill.
2. Confirm repository, active/base ref, current version/build and relevant CI status.
3. Read the current Reclama Blueprint and active specs.
4. Inspect the source modules affected by the requested work.
5. Record constraints and uncertainties instead of guessing.

Produce a concise TLC Initialization Snapshot in `docs/specs/_initialization.md` with repository/ref, version/build, product focus, relevant specs, baseline status, constraints and proposed spec.

### 1. Think
Identify the user problem and affected flows. Check cross-screen, persistence, keyboard, responsive layout, privacy, networking and regression impact. Define non-goals.

### 2. Lock
Create/update a spec in `docs/specs/` before implementation. It must contain status, problem, desired behavior, scope, non-goals, observable acceptance criteria, data impact, UI/UX impact, edge cases and verification plan.

### 3. Code
Implement the smallest coherent change satisfying the locked spec. Preserve behavior outside scope, prefer shared/reusable fixes, and add checks/tests where practical.

### 4. Verify
Compare implementation against every acceptance criterion and record each as PASS or BLOCKED with evidence. A successful build alone is not proof of user-visible correctness.

## Definition of Done
Done requires valid initialization, a locked spec, every criterion PASS/BLOCKED with evidence, regression/persistence impact checked, user-visible documentation updated where relevant, and the PR referencing the spec.

## Spec lifecycle
`DRAFT → LOCKED → IMPLEMENTING → VERIFYING → DONE`

## Reclama conventions
- Spec IDs: `RC-<AREA>-NNN`, for example `RC-CORE-001`.
- Product source of truth: current Reclama Blueprint plus explicit repository product decisions.
- Specs live under `docs/specs/`.
- One spec describes one user-visible behavior or one tightly coupled technical change.
- Bug fixes require a regression criterion.
- Privacy-sensitive complaint narratives must never be added to logs merely for diagnostics.

## Required PR footer
`Spec: RC-AREA-NNN`

Include the acceptance criteria with PASS/BLOCKED status.
