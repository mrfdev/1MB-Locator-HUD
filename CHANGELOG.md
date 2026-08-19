# Changelog

## 1.40.0 — Snapshot Public Beta 3

- Published the tested 1.40.0 build as the third public beta for community feedback.
- Added the default-off `aibo magic` checkbox beside Colors. It smoothly selects an existing HUD theme from the current local underground, cold, warm, or temperate biome category.
- Added a half-second stable-environment delay and one-second color blend to prevent rapid theme flicker at biome borders or around brief underground checks.
- Kept the selected manual color theme intact as a fallback and restore target; disabling the override returns to it immediately without rewriting configuration choices.
- Reused the bounded client biome sampler and local column height data, without scanning neighboring terrain, retaining biome history, or adding networking.
- Replaced positional palette fields with a named, immutable color specification so runtime interpolation and future color roles remain testable outside Minecraft.
- Audited the README, roadmap, migration record, screenshots, build instructions, defaults, and in-game preset help against the completed 1.40.0 implementation.
- Made strict dependency verification portable to Linux CI by pinning the additional Maven metadata artifacts it resolves and narrowly exempting Fabric Loom's platform-dependent, locally derived Minecraft JARs.

## 1.39.1 — development build

- Fixed the preset selector resetting visually to Minimal after applying another preset. The selected preset now survives the configuration screen's widget rebuild.
- Added a production-client UI regression test that selects Builder, presses the real Apply preset button, and verifies that Builder remains selected afterward.

## 1.39.0 — development build

- Added a pinned Java 25 delivery pipeline: Fabric Loom is fixed at 1.17.19, the Gradle distribution has an official SHA-256 requirement, and resolved build dependencies are covered by Gradle verification metadata.
- Enabled strict Java source linting with warnings treated as errors, while excluding only third-party classfile-annotation noise from Gson.
- Added clean-build verification for client-only Fabric metadata, exact dependency floors, required JAR resources, forbidden networking/server/telemetry/command APIs, and accidental location logging.
- Added production-client startup smoke tests both without and with optional Mod Menu. The isolated Fabric test mod is never packaged in the release JAR.
- Added SHA-256 generation beside the runtime JAR and a pinned GitHub Actions workflow that runs the strict build, checksum validation, and both startup variants on Java 25.
- Added durable configuration-migration fixtures and exhaustive main/details row-plan matrices, complementing the existing geometry, sampler-timing, and coordinate-copy tests.

## 1.38.0 — development build

- Added four fixed, fully editable presets: Minimal, Explorer, Builder, and Privacy. Presets configure existing visibility, content, size, and background settings while preserving the selected colors, panel positions, shadows, and coordinate-copy format.
- Added exactly one separate local Saved setup slot in `config/locator-hud-saved-setup.json`, with explicit save and restore actions, overwrite/restore confirmation, atomic persistence, schema validation, malformed-file backup behavior, and protection against overwriting newer-schema setup files after a downgrade.
- Added confirmation before restoring factory defaults. Reset leaves the Saved setup untouched, keeping factory defaults, built-in presets, and the user's one preferred setup distinct.

## 1.37.1 — development build

- Fixed a render-frame crash caused by tiny negative floating-point residue after movement drained out of the speed-smoothing window. The tracker now derives each result from its bounded non-negative sample window instead of carrying a drifting rolling total.
- Added a deterministic regression test that reproduces the reported movement-to-idle sequence and verifies that the resulting HUD snapshot remains valid.

## 1.37.0 — development build

- Added an explicit, remappable coordinate-copy action on `F8` by default. It writes only to the local clipboard and never executes a command, opens chat, or sends a network message.
- Added Plain, namespaced Vanilla TP, and CMI `tppos` copy formats using the selected decimal precision. Command tokens are constrained to safe single arguments, and unsafe player/world values become visible placeholders instead of being escaped or injected.
- Added the copy-format selector to the available global configuration slot beside the paired shadow controls.

## 1.36.0 — development build

- Expanded View direction into `ON`, `ON (with details)`, and `OFF`. Basic `ON` preserves the existing four-way name, while the opt-in detailed mode shows an eight-way direction and compact signed-axis hint such as `Northeast [+X/-Z]`.
- Migrated the legacy direction boolean into a validated three-state setting while retaining its serialized compatibility mirror for older builds.

## 1.35.0 — development build

- Added target-name modes for block, fluid, and entity rows. `API accurate` remains the default and shows the full stable namespaced identifier; `Friendly` uses Minecraft's localized player-facing name with a safe identifier fallback.
- Preserved both structured target representations through sampling and linger so switching modes is immediate and never retains live Minecraft world, state, or entity objects.

## 1.34.0 — development build

- Added a default-off observed movement-speed row to the details panel. It reports smoothed horizontal blocks per second from local position changes and remains entirely separate from CMI speed settings or controls.
- Added default-off 0.5-second target linger for block, fluid, and entity values, preventing auto-hidden target rows from flickering as the crosshair moves over edges.
- Added default-off three-second biome-transition notices that briefly show the previous and current biome without scanning or retaining biome history.
- Kept the configuration screen compact by grouping Biome, Change, and Speed together, and pairing Auto-hide with Linger.

## 1.33.0 — development build

- Added a default-off Overworld–Nether coordinate lens to the main panel. It shows the approximate mathematical X/Z counterpart using the selected decimal precision, only recognizes the two vanilla coordinate spaces, and never implies that a portal exists or a destination is safe.

## 1.32.0 — development build

- Paired each panel's Size and Background sliders when enough width is available, using compact `Size` and `BG` labels while retaining the qualitative preset names, percentages, and full tooltips; narrow layouts keep the sliders stacked.

## 1.31.0 — development build

- Grouped Text shadow and Panel shadow into one standard-width global slot, leaving the adjacent wide-layout slot available for a future global control and saving one row in the narrow layout.

## 1.30.0 — development build

- Standardized decimal-count option labels on numerals, so coordinate and angle precision now consistently use `1 decimal` and `2 decimals`.

## 1.29.0 — development build

- Paired each panel's visibility switch with its position control, using compact `Main panel`/`Details panel` and `Position` labels plus axis-separated corner values such as `Top / Left`.

## 1.28.0 — development build

- Condensed the related view-direction/view-angle switches into one row and the three target switches into one compact Block/Fluid/Entity row, reducing the configuration screen by three control rows while retaining full explanatory tooltips.

## 1.27.0 — development build

- Rebuilt the configuration screen around supported responsive layouts: normal-height controls now scroll beneath a fixed title and footer, retain two panel columns when space permits, and fall back to a centered single column on narrow screens.
- Split global, main, details, and footer construction into focused sections, consolidated dependent-control activation, and replaced the duplicated size/background widgets with one tested discrete-option slider.

## 1.26.0 — development build

- Extracted scale-aware panel measurement, corner placement, offset clamping, oversized-panel handling, and same-corner stacking into an environment-neutral geometry engine with focused boundary tests, while preserving current 60–100% layouts and preparing for future accessibility sizes.

## 1.25.0 — development build

- Replaced renderer-specific row branches with immutable, environment-neutral main and details panel-content plans whose semantic segments now drive visibility, row counts, width measurement, truncation, colors, and drawing from one source of truth.

## 1.24.0 — development build

- Added an immutable client HUD snapshot path: coordinates and angles remain frame-responsive, while biome and crosshair inspection use bounded client-tick caches with connection, dimension, and large-position resets.

## 1.23.0 — development build

- Added an independent, default-on toggle for showing the cardinal view direction while keeping yaw and pitch separately configurable.
- Removed deprecated Gradle task-time project access from resource processing and made clean builds compatible with Gradle's configuration cache.
- Separated validated settings from persistence, added schema-versioned legacy loading, preserved malformed files as dated backups, and covered config recovery and save failures with focused tests.

## 1.22.0 — Snapshot Public Beta 2

- Published the tested 1.22.0 build as the second public beta for community feedback.
- Improved configuration-screen readability with near-white setting names that remain subtly distinct from white values.
- Added README screenshots showing HUD layouts and the full configuration screen.

## 1.21.0 — development build

- Standardized true state labels as green `ON` and false/off state labels as red `OFF`, while keeping qualifiers such as placement and minimal mode white.
- Refreshed the README against the current implementation and aligned Fabric metadata with the documented Fabric API minimum.

## 1.20.0 — development build

- Replaced the main and details size controls with snapping sliders for 60%, 70%, 80%, 90%, and 100%.

## 1.19.0 — development build

- Styled world-name placement states with green `ON`, white placement text, and red `OFF`.

## 1.18.0 — development build

- Added one-second explanatory hover tooltips to every interactive configuration control.

## 1.17.0 — development build

- Added an optional, default-off setting that hides empty target rows in the details panel.

## 1.16.0 — development build

- Added a `None` decimal-precision choice for whole-number decimal XYZ values.

## 1.15.0 — development build

- Added world-name placement choices for before the coordinates, after them, or off.

## 1.14.0 — development build

- Replaced both background controls with snapping sliders for Off, 7%, 24%, 55%, 72%, 88%, and 100% opacity.

## 1.13.0 — development build

- Improved Duo-tone readability by lightening gray key labels while keeping values white.
- Made the yaw and pitch display narrower with tighter spacing and compact pitch arrows.
- Expanded both panel size controls to five choices from 60% through 100%.

## 1.12.1 — Snapshot Public Beta 1

- Published the tested standalone build as the first public beta for community feedback.
- Packaged the selected elaborate compass-and-XYZ-arrows icon.
- Exported Locator HUD from the private 1MB Mods monorepo as an independent Fabric project.
- Added a standalone Gradle build, wrapper, documentation, license notice, and repository migration checklist.

## History through 1.12.0

The mod evolved from a compact XYZ/world/direction HUD into independently configurable main and details panels with:

- decimal and block-coordinate display modes;
- optional world, title-case direction, view-angle precision, biome, and crosshair targets;
- independent panel visibility, position, scale, and background;
- compact backgroundless layouts, shadows, and nine color schemes;
- persistent Mod Menu configuration and an `F7` global toggle;
- public website/issues metadata, readable author/license metadata, and a custom icon.

The detailed pre-extraction changelog remains archived in the source monorepo.
