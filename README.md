# 1MB Locator HUD

1MB Locator HUD is a client-only Fabric mod for Minecraft Java Edition 26.2. It replaces coordinate-heavy F3 use with compact, configurable main and details panels. It sends no network messages, requires no server plugin or permission, and works in singleplayer and multiplayer.

The mod was made for [1MoreBlock.com](https://www.1moreblock.com/), a public Java Edition survival Minecraft server currently running Minecraft 26.2. It remains fully server-independent and can be used on any compatible server or in singleplayer.

![Locator HUD icon](src/main/resources/assets/locatorhud/icon.png)

## Release status

- **Current release:** [1MB Locator HUD 1.22.0 — Snapshot Public Beta 2](https://github.com/mrfdev/1MB-Locator-HUD/releases/tag/v1.22.0), tested and published for community feedback.
- **Previous beta:** [1MB Locator HUD 1.12.1 — Snapshot Public Beta 1](https://github.com/mrfdev/1MB-Locator-HUD/releases/tag/v1.12.1).
- **In development:** 1.23.0.

The feature, configuration, and build references below track the unreleased 1.23.0 development source. Installation and download links continue to point to the tested 1.22.0 Snapshot Public Beta 2. Please report beta feedback and problems through the [issue tracker](https://github.com/mrfdev/1MB-Locator-HUD/issues).

## Features

- Rounded whole-number XYZ, one- or two-decimal XYZ, containing-block XYZ, both coordinate rows, or neither.
- Optional friendly world/dimension name before or after the first coordinate row. When coordinates are hidden, the world name uses its own row.
- Independently toggleable title-case cardinal direction and compact yaw/pitch angles in whole, one-decimal, or two-decimal degrees.
- Optional biome and crosshair-target block, fluid, and entity rows. Auto-hide can suppress empty target rows while leaving an enabled biome row visible.
- Independently visible and positioned main and details panels. If both use the same corner, the details panel stacks vertically with the main panel.
- Independent five-stop size sliders at 60%, 70%, 80%, 90%, and 100% for each panel.
- Independent seven-stop background sliders at `OFF`, 7%, 24%, 55%, 72%, 88%, and 100%. `OFF` uses a compact backgroundless layout.
- Shared text and panel shadows plus nine color schemes: None (all white), Duo-tone, Ocean, Amethyst, Emerald, Ember, Frost, Rose, and Gold.
- Remappable global visibility key binding (`F7` by default) and optional Mod Menu configuration with near-white setting names, color-coded state labels, and one-second hover tooltips.
- Automatically saved, backward-compatible client configuration in `config/locator-hud.json`.

## Requirements

- Minecraft Java Edition 26.2
- Java 25
- [Fabric Loader 0.19.3 or newer](https://fabricmc.net/)
- [Fabric API 0.154.2+26.2](https://modrinth.com/mod/fabric-api)
- [Mod Menu 20.0.1 or newer](https://modrinth.com/mod/modmenu) is optional but recommended

## Installing Snapshot Public Beta 2

1. Install Fabric Loader for Minecraft 26.2.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and [`1MB-Locator-HUD-1.22.0.jar`](https://github.com/mrfdev/1MB-Locator-HUD/releases/download/v1.22.0/1MB-Locator-HUD-1.22.0.jar).
3. Put both JAR files in the client instance's `mods/` folder.
4. Optionally add [Mod Menu](https://modrinth.com/mod/modmenu) for the in-game configuration screen.
5. Launch Minecraft with the Fabric profile.

## Usage

Press `F7` in game to show or hide the entire HUD. Minecraft displays a short enabled/disabled confirmation, and the binding can be changed in the Controls screen under **Locator HUD**.

With Mod Menu installed, open **Mods**, select **1MB Locator HUD**, and use its configuration button. Every setting is saved immediately. **Reset** restores all defaults, while **Done** returns to the previous screen.

Without Mod Menu, the HUD still runs normally with its defaults or previously saved settings, and the `F7` binding remains available. Configuration is stored in `config/locator-hud.json`; close the client before editing that file manually.

## Configuration reference

| Area | Setting | Default | Choices or behavior |
| --- | --- | --- | --- |
| Global | HUD | `ON` | Shows or hides the entire HUD. |
| Global | Colors | Ocean | None (all white), Duo-tone, Ocean, Amethyst, Emerald, Ember, Frost, Rose, or Gold; shared by both panels. |
| Global | Text shadow | `ON` | Shared by both panels. |
| Global | Panel shadow | `ON` | Shared by both panels and available when at least one enabled panel uses a non-`OFF` background. |
| Main | Show main panel | `ON` | Independently shows or hides the main panel. |
| Main | Main position | Top left | Top left, top right, bottom left, or bottom right. |
| Main | Coordinate display | XYZ only | XYZ only, block XYZ only, XYZ plus block, or none. |
| Main | Decimal precision | 1 decimal | `None` rounds XYZ to whole numbers; one or two decimal places are also available. This control is available when coordinate display includes XYZ. |
| Main | World name | `ON (behind)` | `ON (in front)`, `ON (behind)`, or `OFF`. |
| Main | View direction | `ON` | Shows the title-case cardinal direction (North, South, East, or West). |
| Main | View angles | `OFF` | Shows compact yaw and pitch values; when view direction is also enabled, they appear beside it. |
| Main | Angle decimals | `OFF` | Whole degrees when `OFF`, or one or two decimal places; available when view angles are on. |
| Main | Main size | Normal (100%) | Snaps to 60%, 70%, 80%, 90%, or 100%. |
| Main | Main background | Balanced (72%) | `OFF`, 7%, 24%, 55%, 72%, 88%, or 100%. |
| Details | Show details panel | `ON` | Independently enables the details/target panel. It does not render until at least one details row is visible. |
| Details | Details position | Top right | Top left, top right, bottom left, or bottom right. |
| Details | Biome | `OFF` | Shows the biome at the player's current position. |
| Details | Target block, fluid, and entity | All `OFF` | Three independent crosshair-target rows. Empty enabled rows show an em dash unless auto-hide is on. |
| Details | Auto-hide empty values | `OFF` | Hides empty block, fluid, and entity rows; does not hide an enabled biome row. If no rows remain visible, the entire details panel does not render. |
| Details | Details size | Compact (80%) | Snaps to 60%, 70%, 80%, 90%, or 100%. |
| Details | Details background | `OFF (minimal)` | `OFF`, 7%, 24%, 55%, 72%, 88%, or 100%. |

## Screenshots

| Main and biome panels | Decimal and block coordinates |
| :---: | :---: |
| [![Locator HUD showing rounded XYZ coordinates, view angles, and a separate Plains biome panel in the top-left corner.](docs/images/locator-hud-main-and-biome-panels.png)](docs/images/locator-hud-main-and-biome-panels.png) | [![Locator HUD showing decimal XYZ, containing-block coordinates, yaw, and pitch in the top-left corner.](docs/images/locator-hud-coordinate-display-modes.png)](docs/images/locator-hud-coordinate-display-modes.png) |

**Configuration screen with live HUD preview**

[![Minecraft gameplay with the 1MB Locator HUD configuration screen open and both HUD panels visible.](docs/images/locator-hud-configuration-screen.png)](docs/images/locator-hud-configuration-screen.png)

## Commands and networking

The mod registers no commands, sends no custom network messages, performs no telemetry or remote calls, and requires no server-side setup.

## Building from source

The Gradle wrapper is included. With JDK 25 available, run:

```sh
./gradlew clean build
```

On Windows, use `gradlew.bat clean build` instead.

The runtime and source JARs are written to:

```text
build/libs/1MB-Locator-HUD-1.23.0.jar
build/libs/1MB-Locator-HUD-1.23.0-sources.jar
```

To run only the full unit-test suite:

```sh
./gradlew test
```

## Project structure

- `src/main/java`: environment-neutral formatting, layout, option models, and display policies.
- `src/client/java`: Fabric client initialization, HUD rendering, crosshair targeting, configuration persistence, and Mod Menu UI.
- `src/main/resources`: Fabric metadata and the mod icon.
- `src/client/resources`: client translations.
- `src/test/java`: unit tests for formatting, display modes, layout, visibility rules, and slider snapping.

## Project links

- [Public repository](https://github.com/mrfdev/1MB-Locator-HUD)
- [Published releases](https://github.com/mrfdev/1MB-Locator-HUD/releases)
- [Issue tracker](https://github.com/mrfdev/1MB-Locator-HUD/issues)
- [Changelog](CHANGELOG.md)
- [Roadmap](ROADMAP.md)
- [Migration record](MIGRATION.md)

## License

Copyright © 2026 mrfloris. All rights reserved. See [LICENSE](LICENSE).

<sub>1MB Locator HUD was created by mrfloris and Codex.</sub>
