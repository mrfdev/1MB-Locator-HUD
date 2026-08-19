# 1MB Locator HUD

1MB Locator HUD is a client-only Fabric mod for Minecraft 26.2. It replaces coordinate-heavy F3 use with compact, configurable main and details panels. It sends no network messages, requires no server plugin or permission, and works in singleplayer and multiplayer.

![Locator HUD icon](src/main/resources/assets/locatorhud/icon.png)

## Features

- Decimal XYZ, containing-block XYZ, both rows, or neither.
- Optional friendly world/dimension name.
- Title-case cardinal direction with optional yaw and pitch.
- Optional biome and crosshair-target block, fluid, and entity rows.
- Independently visible, positioned, scaled, and styled main and details panels.
- Backgroundless compact mode, panel/text shadows, and nine color schemes.
- Configurable `F7` global toggle and a Mod Menu configuration screen.
- Persistent client configuration in `config/locator-hud.json`.

## Requirements

- Minecraft 26.2
- Java 25
- Fabric Loader 0.19.3 or newer
- Fabric API 0.154.2+26.2
- Mod Menu 20.0.1 or newer is optional but recommended

## Installation

1. Install Fabric Loader for Minecraft 26.2.
2. Put Fabric API and `1MB-Locator-HUD-1.12.1.jar` in the client's `mods/` folder.
3. Optionally install Mod Menu.
4. Press `F7` in game, or open **Mods → 1MB Locator HUD** to configure it.

## Build

The Gradle wrapper is included. With JDK 25 available:

```sh
./gradlew clean build
```

The runtime JAR is written to:

```text
build/libs/1MB-Locator-HUD-1.12.1.jar
```

Run the focused tests with:

```sh
./gradlew test
```

## Project structure

- `src/main/java`: environment-neutral formatting and layout logic.
- `src/client/java`: Minecraft client entrypoint, renderer, targeting, and configuration UI.
- `src/main/resources`: Fabric metadata and the mod icon.
- `src/client/resources`: client translations.
- `src/test/java`: unit tests for formatting and layout behavior.

## Project links

- [Public repository](https://github.com/mrfdev/1MB-Locator-HUD)
- [Issue tracker](https://github.com/mrfdev/1MB-Locator-HUD/issues)
- [Roadmap](ROADMAP.md)

Migration details from the former monorepo are recorded in [MIGRATION.md](MIGRATION.md).

## License

Copyright © 2026 mrfloris. All rights reserved. See [LICENSE](LICENSE).
