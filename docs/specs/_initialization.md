# TLC Initialization Snapshot

- Repository: `LoloscarPrr/reclama-android`
- Working ref: `feature/reliability-adaptive-skills`
- Base ref: `feature/ai-intake-v0.2.0` @ `874d63cc612fe27291250372a178df4708f4a2a8`
- App version/build: `0.2.0-alpha` / versionCode `2`
- Product focus: AI-assisted Chilean consumer complaint intake and local expediente flow.
- Relevant active work: AI Intake v0.2.0 draft PR plus cross-cutting reliability/responsive foundation.
- Baseline: prior Android CI for AI Intake reported PASS; current branch build not yet verified.
- Constraints: Firebase project/config (`app/google-services.json`) is not available in-repo, so live Crashlytics reporting cannot be activated yet without external Firebase configuration. Complaint narratives must not be logged as diagnostics.
- Proposed spec: `RC-CORE-001 — Reliability, adaptive layout and repository skills`.
