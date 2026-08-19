# Roadmap

The current public release is 1.22.0 — Snapshot Public Beta 2.
The current development version is 1.23.0.

This roadmap records both accepted work and explicit non-goals. New functionality must remain standalone, client-only, backward compatible, and free of telemetry or custom networking.

## Approved lightweight features

- [ ] Add an optional Overworld–Nether coordinate lens, defaulting to `OFF`.
  - In the Overworld, show the mathematically corresponding Nether X/Z coordinates.
  - In the Nether, show the mathematically corresponding Overworld X/Z coordinates.
  - Mark the result as approximate and never imply that a portal exists or that the destination is safe.
  - Limit automatic conversion to the vanilla Overworld and Nether unless another dimension mapping is explicitly configured in the future.
- [ ] Add an optional observed movement-speed row to the details panel, defaulting to `OFF`.
  - Measure local movement in blocks per second and smooth short client/server corrections.
  - Keep it read-only and clearly separate from CMI multipliers or speed-changing controls.
- [ ] Expand `View direction` from `ON`/`OFF` to `ON`, `ON (with details)`, and `OFF`.
  - Preserve `ON` as the default and current four-way display.
  - Use `ON (with details)` for the optional eight-way direction and compact axis hint, keeping the enhanced detail mode opt-in.
- [ ] Add an explicit, user-triggered coordinate-copy action.
  - Never copy automatically and never send chat or network messages.
  - Offer a plain coordinate format.
  - Offer a namespaced vanilla teleport-command format using `/minecraft:...`.
  - Offer a CMI template in the form `/cmi tppos -p:<playername> <x> <y> <z> <world>`.
  - Verify the exact vanilla and CMI command syntax, player/world escaping, permissions, and supported coordinate formats before shipping either command template.
- [ ] Add an optional biome-transition notice, defaulting to `OFF`, such as `Plains → Jungle` when the biome beneath the player changes.
  - Only react to the current client-known biome; do not scan, map, or retain biome-discovery history.

## Approved UI and quality-of-life work

- [ ] Add a remappable `Open Locator HUD settings` key so configuration remains accessible without Mod Menu.
  - Prefer an unbound default to avoid key conflicts; keep Mod Menu integration optional.
- [ ] Add a direct panel-placement editor if it can be implemented cleanly with supported client APIs.
  - Allow dragging the main and details panels over the live configuration preview.
  - Support edge/corner snapping and small, clamped X/Y offsets.
  - Keep the normal gameplay HUD non-interactive so it never captures attack or use clicks.
- [ ] Add configurable minimum and maximum widths for each panel.
  - Preserve automatic sizing as the default.
  - Validate and clamp values so minimum width cannot exceed maximum width or push a panel irrecoverably off-screen.
- [ ] Add optional target-value linger so empty auto-hidden rows do not flicker when the crosshair passes over block edges.
  - Keep the current immediate-hide behavior as the backward-compatible default.
  - Use one simple, bounded linger duration or a very small set of choices.
- [ ] Add target-name modes: `Friendly` and `API accurate`.
  - Friendly mode should use localized, player-facing names where reliable.
  - API-accurate mode should show stable registered identifiers.
  - Preserve the current registry-oriented behavior as the default unless testing demonstrates a clearer compatible choice.
- [ ] Make Reset safer without creating a confusing profile manager.
  - Require confirmation before `Reset defaults`.
  - Provide exactly one local `Saved setup` slot with explicit `Save current setup` and `Restore saved setup` actions.
  - Keep the saved setup separate from factory defaults and built-in presets, and show clearly when restoring will replace current settings.
- [ ] Add a small set of built-in presets such as Minimal, Explorer, Builder, and Privacy.
  - Presets only apply existing settings; all controls remain individually editable afterward.
  - A Privacy preset may hide existing rows, but must not introduce the rejected dynamic Privacy Shield or automatic masking behavior.
  - Avoid named slots, syncing, automatic switching, or a general preset-management system.
- [ ] Move every player-facing option value to translation keys and accept community locale files.
  - Cover enum choices, positions, palette names, precision names, preset names, tooltips, and confirmation text.
- [ ] Add a top-level `Accessibility settings` switch, defaulting to `OFF`, to expose additional accessibility controls without cluttering the normal screen.
  - Include larger panel sizes such as 110%, 125%, and 150%, with screen-edge clamping.
  - Include improved keyboard/narrator guidance and explanations for disabled controls.
  - Do not add a forced high-contrast lock; existing palette/background controls remain available normally.
- [ ] Replace the fixed dense layout with a responsive configuration screen.
  - Retain two columns when space permits.
  - Use scrolling, tabs, or a single-column fallback for narrow screens, long translations, and accessibility sizes.

## Approved security, privacy, and engineering hardening

- [ ] Verify Minecraft 26.2's exact server-provided reduced-debug state and, when reliably available, respect it by default for coordinates and target details.
- [ ] Preserve malformed configuration files as dated `.broken.json` backups, load safe defaults, and show one concise warning.
- [ ] Report configuration-save failures instead of silently implying that changes were persisted; retain unsaved values for retry where practical.
- [ ] Add a configuration schema version and explicit migration tests for legacy, missing, invalid, and future-facing fields.
- [ ] Cache biome and crosshair-target inspection at a modest update rate or until relevant input changes, while keeping coordinates and direction visually responsive.
- [ ] Add CI checks for Java 25 clean builds, unit tests, required JAR contents, client-only Fabric metadata, and startup both with and without Mod Menu.
- [ ] Add a regression check that rejects custom networking, telemetry, custom command registration, server-only dependencies, and accidental logging of player coordinates.
- [ ] Publish a checksum file for releases and investigate GitHub/Sigstore build attestations, Gradle wrapper validation, and dependency verification.

## Deferred and verification-gated CMI idea

Do not implement or advertise CMI speed-changing support until every gate below is resolved. The read-only observed movement-speed row above is independent of this idea.

- [ ] Verify CMI's supported command or API, live effective-speed source, permissions, denial response, and server acknowledgement.
- [ ] Fail closed or show unavailable whenever CMI or the required permission cannot be confirmed.
- [ ] Only after verification, consider an optional smaller-font line showing the acknowledged CMI walk or fly multiplier.
- [ ] Only accept mouse interaction while an explicit screen such as chat is open, so normal world clicks are never captured.
- [ ] Only after verified server acknowledgement, consider cycling `1×`, `2.5×`, and `5×` presets.
- [ ] Make the active movement type, requested value, acknowledged value, denial, and unavailable state visually unambiguous.

## Explicitly rejected or out of scope

- A return marker, marker storage, waypoint manager, minimap, route-finding, or location-history system.
- Safe Share/Privacy Shield modes and automatic privacy masking in the configuration preview.
- Chunk-edge or in-chunk coordinate tools.
- Straight-line, lane-drift, tunnel, bridge, or highway guides.
- Target distance, hit-face, or other target-geometry additions.
- Adaptive-precision or hold-to-reveal coordinate modes.
- A forced high-contrast accessibility lock.
- Ore or entity scanning, seed/slime-derived information, entity-health overlays, or undiscovered-world information.
- Automatic clipboard writes, automatic chat insertion, automatic coordinate sharing, telemetry, remote calls, or custom network messages.
- CMI speed changes without verified permissions, denial handling, and authoritative server acknowledgement.
