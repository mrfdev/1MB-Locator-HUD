# Standalone repository migration

This project was exported from the private `1MB-Mods` monorepo at Locator HUD version 1.12.1.

## Create the local project

1. Extract the archive under `/Users/floris/Projects/Codex/1MB-Locator-HUD`.
2. Open that folder as a new Codex project.
3. Run `./gradlew clean build` with JDK 25.
4. Confirm `build/libs/1MB-Locator-HUD-1.12.1.jar` exists and loads in the Fabric 26.2 profile.

## Create the public GitHub repository

1. Create the public repository, anticipated as `mrfdev/1MB-Locator-HUD`.
2. If the final owner or repository name differs, update all three contact URLs in `src/main/resources/fabric.mod.json`, plus README and AGENTS references.
3. Initialize Git, review every staged file, commit, and push.
4. Confirm the public GitHub source, website, and issues links work from Mod Menu.
5. Tag or otherwise preserve the first independently verified release.

## Remove the old monorepo module only afterward

Do not remove the original `locator-hud/` module until all of these are true:

- the standalone project builds from a clean checkout;
- the public remote contains the complete source and Gradle wrapper;
- the 1.12.1 JAR built from the standalone checkout works in Minecraft;
- the new repository URL is final;
- the standalone history has at least one pushed commit.

Then remove the old module, its Gradle include/version property, and its monorepo-only documentation references in one reviewed change. Keep a pointer to the new public repository in the old monorepo changelog or README.
