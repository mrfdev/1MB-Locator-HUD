# Standalone repository migration

The migration from the private `1MB-Mods` monorepo was completed on 2026-08-19 at Locator HUD version 1.12.1.

- Standalone project: `/Users/floris/Projects/Codex/1MB-Locator-HUD`
- Public repository: [mrfdev/1MB-Locator-HUD](https://github.com/mrfdev/1MB-Locator-HUD)
- Standalone clean build: verified
- Public source and Gradle wrapper: verified
- Original monorepo module: removed after verification

## Reproducing the local project

1. Extract the archive under `/Users/floris/Projects/Codex/1MB-Locator-HUD`.
2. Open that folder as a new Codex project.
3. Run `./gradlew clean build` with JDK 25.
4. Confirm `build/libs/1MB-Locator-HUD-1.12.1.jar` exists and loads in the Fabric 26.2 profile.

## Publishing under a different repository

1. Create the new public repository.
2. If the final owner or repository name differs, update all three contact URLs in `src/main/resources/fabric.mod.json`, plus README and AGENTS references.
3. Initialize Git, review every staged file, commit, and push.
4. Confirm the public GitHub source, website, and issues links work from Mod Menu.
5. Tag or otherwise preserve the first independently verified release.

## Safety gate used for the original removal

Do not remove the original `locator-hud/` module until all of these are true:

- the standalone project builds from a clean checkout;
- the public remote contains the complete source and Gradle wrapper;
- the 1.12.1 JAR built from the standalone checkout works in Minecraft;
- the new repository URL is final;
- the standalone history has at least one pushed commit.

The old module, its Gradle include/version property, and its monorepo-only documentation references were removed only after this checklist passed. The old monorepo retains a concise pointer to this public repository in its changelog and README.
