# RC-CORE-001 — Reliability, adaptive layout and repository skills

Status: DONE
Owner: Reclama

## Problem
Reclama currently uses a single phone-oriented layout, has no repository-native spec workflow, and does not yet have production crash reporting infrastructure. These cross-cutting concerns should be added without changing complaint-domain behavior.

## Desired behavior
1. The repository follows a reusable TLC spec-driven workflow.
2. UI uses one shared adaptive layout policy with compact, regular and wide classes.
3. Firebase Crashlytics build integration is prepared safely, but remains inactive until a valid Firebase Android configuration file is supplied.

## Scope
- Add `AGENTS.md`, TLC skill and spec documentation.
- Add a shared Compose adaptive-layout policy.
- Apply it to Home, Intake, Review and Detail screens.
- Compact: width < 360dp; regular: 360–839dp; wide: >= 840dp.
- Wide screens center content and cap readable width at 1040dp.
- Compact screens reduce horizontal padding.
- Add Firebase/Crashlytics plugin coordinates and SDK wiring in a way that does not break CI when `google-services.json` is absent.
- Document the external configuration gate.

## Non-goals
- No domain model or persistence changes.
- No redesign of individual cards or copy.
- No remote AI/backend work.
- No Firebase project creation or synthetic configuration values.
- No logging of complaint narratives, company names or other case content to Crashlytics.

## Acceptance criteria
- [x] AC1 — Repository contains and references the TLC spec-driven skill.
- [x] AC2 — One shared layout classifier uses <360 / 360–839 / >=840dp breakpoints.
- [x] AC3 — All current app screens inherit the shared adaptive stage.
- [x] AC4 — Wide layouts are centered and capped to 1040dp; compact layouts use reduced horizontal padding.
- [x] AC5 — Existing navigation, persistence and AI Intake behavior are unchanged by this increment.
- [x] AC6 — Crashlytics dependencies/plugins are build-ready and only applied when Firebase config exists.
- [x] AC7 — Missing Firebase config is explicitly documented as BLOCKED for live crash reporting.
- [x] AC8 — Android CI/build passes after the change.

## Data / persistence impact
None.

## UI / UX impact
Only outer screen width/alignment and compact horizontal spacing change. Product flows and copy remain unchanged.

## Privacy impact
Crash diagnostics must not intentionally attach raw complaint narratives or other case content as keys/logs.

## Edge cases / regressions
- Width changes should cause recomposition without resetting current in-memory screen state.
- Safe drawing insets remain applied.
- Intake text field retains available vertical space and keyboard behavior.
- Firebase absence must not break debug CI builds.

## Verification plan
- Inspect shared classifier and all screen wrappers.
- Inspect Gradle conditions for Firebase configuration.
- Run/observe GitHub Actions Android build and unit tests.
- Record each criterion PASS/BLOCKED after implementation.

## Verification result
- AC1: PASS — `AGENTS.md` points repository changes to `.agents/skills/tlc-spec-driven/SKILL.md`, and initialization/spec files are present.
- AC2: PASS — the shared classifier uses the locked compact/regular/wide breakpoints.
- AC3: PASS — Home, Intake, Review and Detail route through the shared adaptive stage.
- AC4: PASS by code inspection — wide content is centered/capped at 1040dp and compact screens receive reduced horizontal padding.
- AC5: PASS by diff inspection — no domain or persistence files changed; screen actions and AI Intake flow remain in place.
- AC6: PASS — Firebase/Crashlytics Gradle wiring is conditional on `app/google-services.json`, allowing non-Firebase CI builds to continue.
- AC7: PASS — live Crashlytics reporting remains externally BLOCKED until a real Firebase Android app configuration is supplied; no fake configuration is committed.
- AC8: PASS — GitHub Actions run `33519780944` completed successfully for commit `5a18da24a720fb10ec324fce88b9c21911bb4c33`, building the debug APK and unit tests.

## Remaining external gate
Live Firebase Crashlytics event delivery cannot be verified until a real Firebase project contains Android app `cl.reclama.app` and its `google-services.json` is supplied securely. Repository/build integration itself is complete.
