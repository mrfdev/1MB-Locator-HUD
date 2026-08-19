# Locator HUD project instructions

## Scope

- This repository contains only the client-side 1MB Locator HUD Fabric mod.
- Keep it independent from the 1MB Mods staff client, Paper bridge, server permissions, and custom network protocols.
- The mod must remain safe to install on an ordinary Fabric client and must not require a server-side component.

## Compatibility

- Target Minecraft 26.2, Java 25, Fabric Loader 0.19.3, and Fabric API 0.154.2+26.2 until an explicit upgrade is requested.
- Mod Menu is an optional compile/runtime integration and must not become a hard dependency.
- Prefer supported Fabric and Minecraft client APIs over mixins, reflection, or internals.

## Development

- Preserve the split main/client source sets.
- Keep formatting and layout calculations environment-neutral where practical and cover them with focused unit tests.
- Keep configuration backward compatible: new primitive switches need safe initializers, and new enum fields need validation fallbacks.
- Do not add telemetry, network calls, server-only data assumptions, or undiscovered-world information.
- Treat Minecraft's supported server-provided reduced-debug state as authoritative: suppress coordinate rows, the coordinate lens, coordinate copying, and target sampling/display while it is active. Do not add a client-side bypass without an explicit, reviewed change to this policy.
- Use `./gradlew clean build` before handing off a release JAR.
- Keep `README.md`, `CHANGELOG.md`, `gradle.properties`, and Fabric metadata versions aligned.
- Confirm the packaged JAR contains `fabric.mod.json` and `assets/locatorhud/icon.png`.

## Release identity

- Mod ID: `locatorhud`
- Archive name: `1MB-Locator-HUD-<version>.jar`
- Author shown to users: `mrfloris`
- License: All Rights Reserved
- Public repository: `https://github.com/mrfdev/1MB-Locator-HUD`

## Agent skills

### Issue tracker

Issues and specs are tracked in GitHub Issues for `mrfdev/1MB-Locator-HUD`. See `docs/agents/issue-tracker.md`.

### Domain docs

This is a single-context repository using an optional root `CONTEXT.md` and system-wide ADRs under `docs/adr/`. See `docs/agents/domain.md`.
