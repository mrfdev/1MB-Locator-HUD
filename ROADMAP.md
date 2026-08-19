# Roadmap

The current public release is 1.47.0 — Snapshot Public Beta 4. This prerelease requires testing.

This roadmap records both accepted work and explicit non-goals. New functionality must remain standalone, client-only, backward compatible, and free of telemetry or custom networking.

## Approved lightweight features

- [x] Add an optional Overworld–Nether coordinate lens, defaulting to `OFF`.
  - In the Overworld, show the mathematically corresponding Nether X/Z coordinates.
  - In the Nether, show the mathematically corresponding Overworld X/Z coordinates.
  - Mark the result as approximate and never imply that a portal exists or that the destination is safe.
  - Limit automatic conversion to the vanilla Overworld and Nether unless another dimension mapping is explicitly configured in the future.
  - Show it as a main-panel row and format X/Z with the selected Decimal precision.
- [x] Add an optional observed movement-speed row to the details panel, defaulting to `OFF`.
  - Measure horizontal local movement in blocks per second and smooth short client/server corrections over a 10-tick window.
  - Keep it read-only and clearly separate from CMI multipliers or speed-changing controls.
- [x] Expand `View direction` from `ON`/`OFF` to `ON`, `ON (with details)`, and `OFF`.
  - Preserve `ON` as the default and current four-way display.
  - Use `ON (with details)` for the optional eight-way direction and compact axis hint, keeping the enhanced detail mode opt-in.
  - Represent the new state with a validated enum and explicitly migrate the existing boolean setting.
- [x] Add an explicit, user-triggered coordinate-copy action.
  - Never copy automatically and never send chat or network messages.
  - Offer a plain coordinate format.
  - Offer a namespaced vanilla teleport-command format using `/minecraft:...`.
  - Offer a CMI template in the form `/cmi tppos -p:<playername> <x> <y> <z> <world>`.
  - Verify the exact vanilla and CMI command syntax, player/world escaping, permissions, and supported coordinate formats before shipping either command template.
- [x] Add an optional biome-transition notice, defaulting to `OFF`, such as `Plains → Jungle` when the biome beneath the player changes.
  - Only react to the current client-known biome; do not scan, map, or retain biome-discovery history.
  - Show the notice in the details panel for three seconds, temporarily replacing the normal biome value when that row is also enabled.
- [x] Add an optional biome-aware theme override, defaulting to `OFF`.
  - Expose it as a small checkbox with no visible label and the exact hover tooltip `aibo magic`.
  - Keep an accessible narrated name even though the control is visually nameless.
  - While enabled, choose from the existing color themes according to broad local categories such as underground, above-ground cold, warm, and neutral biomes.
  - Define a deterministic category priority and a safe fallback for dimensions or biomes that cannot be classified reliably from current client-known data.
  - Transition gradually between themes and use hysteresis or a small delay so biome borders and brief underground checks do not cause rapid color flicker.
  - Treat this as a runtime visual override: do not repeatedly rewrite the saved manual theme, and restore the user's selected theme immediately when the checkbox is disabled.
  - Reuse the existing bounded biome sampling and do not scan surrounding terrain or retain biome history.

## Approved UI and quality-of-life work

- [x] Add a remappable `Open Locator HUD settings` key so configuration remains accessible without Mod Menu.
  - Prefer an unbound default to avoid key conflicts; keep Mod Menu integration optional.
- [x] Add a direct panel-placement editor if it can be implemented cleanly with supported client APIs.
  - Allow dragging the main and details panels over the live configuration preview.
  - Support edge/corner snapping and small, clamped X/Y offsets.
  - Keep the normal gameplay HUD non-interactive so it never captures attack or use clicks.
- [x] Add configurable minimum and maximum widths for each panel.
  - Preserve automatic sizing as the default.
  - Validate and clamp values so minimum width cannot exceed maximum width or push a panel irrecoverably off-screen.
- [x] Add optional target-value linger so empty auto-hidden rows do not flicker when the crosshair passes over block edges.
  - Keep the current immediate-hide behavior as the backward-compatible default.
  - Use one simple, bounded 0.5-second linger duration.
- [x] Add target-name modes: `Friendly` and `API accurate`.
  - Friendly mode should use localized, player-facing names where reliable.
  - API-accurate mode should show stable registered identifiers.
  - Preserve the current registry-oriented behavior as the default unless testing demonstrates a clearer compatible choice.
- [x] Make Reset safer without creating a confusing profile manager.
  - Require confirmation before `Reset defaults`.
  - Provide exactly one local `Saved setup` slot with explicit `Save current setup` and `Restore saved setup` actions.
  - Keep the saved setup separate from factory defaults and built-in presets, and show clearly when restoring will replace current settings.
- [x] Add a small set of built-in presets such as Minimal, Explorer, Builder, and Privacy.
  - Presets only apply existing settings; all controls remain individually editable afterward.
  - A Privacy preset may hide existing rows, but must not introduce the rejected dynamic Privacy Shield or automatic masking behavior.
  - Avoid named slots, syncing, automatic switching, or a general preset-management system.
- [x] Move every player-facing option value to translation keys and accept community locale files.
  - Cover enum choices, positions, palette names, precision names, preset names, tooltips, and confirmation text.
- [x] Add a top-level `Accessibility settings` switch, defaulting to `OFF`, to expose additional accessibility controls without cluttering the normal screen.
  - Include larger panel sizes such as 110%, 125%, and 150%, with screen-edge clamping.
  - Include improved keyboard/narrator guidance and explanations for disabled controls.
  - Do not add a forced high-contrast lock; existing palette/background controls remain available normally.
- [x] Replace the fixed dense layout with a responsive configuration screen.
  - Retain two columns when space permits.
  - Use scrolling, tabs, or a single-column fallback for narrow screens, long translations, and accessibility sizes.
  - Group compact, closely related switches into shared rows when their labels remain clear and their tooltips retain the full explanation.
  - Pair the longer Size and Background sliders only when the available panel width keeps their compact labels readable.

## Approved code-quality modernization

Complete these in order where practical. Keep each change focused, preserve the current visuals and configuration behavior, and avoid introducing a general framework or runtime dependency.

1. [x] Remove deprecated Gradle task-time project access from resource processing.
   - Capture resource expansion values during configuration rather than reading `project` from the task action.
   - Verify both a configuration-cache clean build and the normal release build without deprecation warnings.
2. [x] Separate configuration data, persistence, validation, and migration responsibilities.
   - Introduce a testable config store with an injected path, explicit UTF-8, structured load/save results, and the existing atomic replacement behavior.
   - Preserve the current flat JSON keys while adding the schema version, migrations, malformed-file backups, future-schema handling, and a clean seam for the in-game save-failure reporting still tracked below.
   - Provide a validated settings snapshot that Reset, built-in presets, and the single Saved setup slot can apply without manually copying an expanding list of fields.
3. [x] Introduce a client HUD sampler that produces immutable snapshots for the renderer.
   - Keep coordinates, yaw, and pitch on a visually responsive path.
   - Cache biome and crosshair inspection at a modest rate or until relevant input changes.
   - Own movement-speed smoothing, target linger, biome-transition timing, and session resets on disconnect, teleport, or dimension change here rather than in the renderer.
   - Minecraft 26.2's computed hit result was verified as interaction-range and entity-aware, so retain the established independent 20-block block/fluid raycasts to avoid changing visible targeting behavior.
4. [x] Replace fixed renderer branches with environment-neutral panel-content plans.
   - Model ordered rows and text segments with semantic color roles so measurement, row counts, visibility, and drawing use one source of truth.
   - Add portal-lens, movement-speed, biome-notice, and target rows through main/details content builders instead of adding parallel renderer conditionals.
5. [x] Extract a scale-aware, testable panel geometry engine.
   - Handle automatic and configured widths, 60–150% scales, screen-edge clamping, offsets, same-corner stacking, oversized panels, and narrow-width truncation.
   - Use it as the shared foundation for accessibility sizes, minimum/maximum widths, and direct panel placement.
6. [x] Add batched or briefly debounced configuration persistence before implementing drag placement and additional sliders.
   - Keep live preview, but save once after a completed interaction or grouped preset/reset operation instead of once per intermediate value.
   - Keep this on the client thread; the small local JSON file does not justify a background executor and its synchronization risks.
7. [x] Modularize the configuration UI before adding the remaining controls.
   - Split global, main, details, and footer construction into focused sections, leave a clean insertion point for future accessibility controls, and adopt normal-height widgets in a responsive scroll/single-column layout.
   - Replace the duplicated scale/background slider implementations with one small tested discrete-option slider.
   - Recompute dependent control states in one place after changes instead of scattering button activation updates through callbacks.
8. [x] Move hard-coded player-facing enum values to translation keys without coupling environment-neutral code to Minecraft components.
   - Preserve serialized enum identifiers for backward compatibility and update tests so they validate behavior and keys rather than English display text.
9. [x] Preserve structured target data until display formatting.
   - Capture a stable registered identifier and friendly localized text instead of shortening immediately to one string.
   - Do not retain live entity, block-state, fluid-state, or world objects for caching or linger.
10. [x] Centralize client key mappings and user-triggered actions as the settings and coordinate-copy bindings are added.
    - Keep coordinate export formatting pure and tested; clipboard access remains explicit, local, and isolated at the client boundary.
11. [x] Move client-independent configuration types such as palette and corner data into the environment-neutral source set where that improves focused testing.
    - Replace error-prone positional palette fields with a small named palette specification if new dynamic-theme roles make the current enum harder to maintain.
12. [x] Strengthen verification without adding runtime bloat.
    - Added versioned config migration fixtures, exhaustive row-plan matrices, geometry boundary tests, sampler timing tests, and focused copy-format tests.
    - Enabled broad Java source linting and made every source warning, including deprecation warnings, fail the build and CI.
    - Added a small isolated client smoke-test layer for startup and focused configuration-screen regressions with and without optional Mod Menu while keeping most coverage as fast unit tests.
    - Pinned Fabric Loom to the resolved 1.17.19 release and added Gradle distribution and dependency checksum verification.

Optimization rule: profile before pursuing formatting micro-optimizations. Bounded world, biome, and target sampling is already implemented; pursue further hot-path changes only after measurement. A dependency-injection framework, event bus, background config writer, or broad rendering rewrite is not planned.

## Approved security, privacy, and engineering hardening

- [x] Verify Minecraft 26.2's exact server-provided reduced-debug state and, when reliably available, respect it by default for coordinates and target details.
  - Use the supported player state initialized during login and updated by vanilla while connected; do not inspect packets or add custom networking.
  - Suppress decimal/block coordinates, the coordinate lens, explicit coordinate copying, and block/fluid/entity target sampling and display while the restriction is active.
  - Clear target linger immediately, leave the clipboard unchanged on a refused copy, and retain configured choices so they return when full debug information becomes available.
  - Keep the policy automatic and non-bypassable in Locator HUD. World name, direction/angles, biome information, and locally observed movement speed remain available.
- [x] Preserve malformed configuration files as dated `.broken.json` backups, load safe defaults, and show one concise log warning.
- [ ] Surface configuration-save failures in game instead of relying only on the structured failure result and concise log warning; retain unsaved values for retry where practical.
- [x] Add a configuration schema version and explicit migration tests for legacy, missing, invalid, and future-facing fields.
- [x] Cache biome and crosshair-target inspection at a modest update rate or until relevant input changes, while keeping coordinates and direction visually responsive.
- [x] Add CI checks for Java 25 clean builds, unit tests, required JAR contents, client-only Fabric metadata, and startup both with and without Mod Menu.
- [x] Add a regression check that rejects custom networking, telemetry, custom command registration, server-only dependencies, and accidental logging of player coordinates.
- [ ] Publish cryptographic delivery evidence for public releases.
  - [x] Generate a SHA-256 file beside every runtime JAR.
  - [x] Validate the Gradle distribution and resolved build dependencies by checksum.
  - [ ] Add GitHub/Sigstore artifact attestations only after designing and reviewing the release workflow that will publish them.

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
