# Changelog

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
