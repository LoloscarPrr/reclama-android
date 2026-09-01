# Reclama Agent Instructions

All repository changes must follow `.agents/skills/tlc-spec-driven/SKILL.md`.

Before the first code change in a new working session:
1. Run the skill's Phase 0: Initialize.
2. Produce or reuse a valid TLC Initialization Snapshot using `docs/specs/_initialization.md`.
3. Refresh initialization if the base ref, version/build metadata, Blueprint/product intent, or relevant module changed materially.

Then, before changing product behavior or fixing a bug:
1. Read the relevant existing code and product documentation.
2. Create/update a spec under `docs/specs/`.
3. Lock scope and acceptance criteria.
4. Implement against that spec.
5. Verify every criterion before declaring completion.

The current Reclama Blueprint and repository product decisions define product intent. When code, old notes and the current blueprint disagree, do not guess: preserve working behavior unless the active spec explicitly changes it.

Never mark work Done merely because it builds. Done requires initialization context plus acceptance-criteria verification.
