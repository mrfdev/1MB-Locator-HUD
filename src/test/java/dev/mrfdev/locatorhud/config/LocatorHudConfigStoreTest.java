package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class LocatorHudConfigStoreTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void returnsValidatedDefaultsWithoutWritingWhenTheFileIsMissing() {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        LocatorHudConfigStore.LoadResult result = new LocatorHudConfigStore(path).load();

        assertSame(LocatorHudConfigStore.LoadStatus.MISSING, result.status());
        assertTrue(result.requiresWriteBack());
        assertFalse(Files.exists(path));
        assertEquals(2, LocatorHudConfigStore.CURRENT_SCHEMA_VERSION);
        assertTrue(result.settings().enabled());
        assertFalse(result.settings().coordinateLensEnabled());
        assertSame(CoordinateCopyFormat.PLAIN, result.settings().coordinateCopyFormat());
        assertFalse(result.settings().biomeThemeOverrideEnabled());
        assertFalse(result.settings().biomeTransitionEnabled());
        assertFalse(result.settings().movementSpeedEnabled());
        assertFalse(result.settings().targetLingerEnabled());
        assertSame(TargetNameMode.API_ACCURATE, result.settings().targetNameMode());
        assertSame(WorldNameDisplay.BEHIND, result.settings().worldNameDisplay());
        assertSame(ViewDirectionDisplay.ON, result.settings().viewDirectionDisplay());
        assertSame(ColorPalette.OCEAN, result.settings().palette());
    }

    @Test
    void roundTripsTheCurrentFlatSettingsDocument() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        LocatorHudConfigStore store = new LocatorHudConfigStore(path);
        LocatorHudSettings settings = LocatorHudSettings.defaults();
        settings.setEnabled(false);
        settings.setCoordinateDisplay(CoordinateDisplayMode.BOTH);
        settings.setPrecision(CoordinatePrecision.TWO_DECIMALS);
        settings.setCoordinateLensEnabled(true);
        settings.setCoordinateCopyFormat(CoordinateCopyFormat.CMI_TPPOS);
        settings.setBiomeThemeOverrideEnabled(true);
        settings.setBiomeTransitionEnabled(true);
        settings.setMovementSpeedEnabled(true);
        settings.setTargetLingerEnabled(true);
        settings.setTargetNameMode(TargetNameMode.FRIENDLY);
        settings.setWorldNameDisplay(WorldNameDisplay.IN_FRONT);
        settings.setViewDirectionDisplay(ViewDirectionDisplay.WITH_DETAILS);
        settings.setHudScale(HudScale.SMALL);
        settings.setPalette(ColorPalette.DUO_TONE);
        settings.setDetailsBackgroundOpacity(BackgroundOpacity.STRONG);

        LocatorHudConfigStore.SaveResult saveResult = store.save(settings);
        LocatorHudConfigStore.LoadResult loadResult = store.load();

        assertSame(LocatorHudConfigStore.SaveStatus.SAVED, saveResult.status());
        assertSame(LocatorHudConfigStore.LoadStatus.LOADED, loadResult.status());
        assertFalse(loadResult.settings().enabled());
        assertSame(CoordinateDisplayMode.BOTH, loadResult.settings().coordinateDisplay());
        assertSame(CoordinatePrecision.TWO_DECIMALS, loadResult.settings().precision());
        assertTrue(loadResult.settings().coordinateLensEnabled());
        assertSame(CoordinateCopyFormat.CMI_TPPOS, loadResult.settings().coordinateCopyFormat());
        assertTrue(loadResult.settings().biomeThemeOverrideEnabled());
        assertTrue(loadResult.settings().biomeTransitionEnabled());
        assertTrue(loadResult.settings().movementSpeedEnabled());
        assertTrue(loadResult.settings().targetLingerEnabled());
        assertSame(TargetNameMode.FRIENDLY, loadResult.settings().targetNameMode());
        assertSame(WorldNameDisplay.IN_FRONT, loadResult.settings().worldNameDisplay());
        assertSame(ViewDirectionDisplay.WITH_DETAILS, loadResult.settings().viewDirectionDisplay());
        assertSame(HudScale.SMALL, loadResult.settings().hudScale());
        assertSame(ColorPalette.DUO_TONE, loadResult.settings().palette());
        assertSame(BackgroundOpacity.STRONG, loadResult.settings().detailsBackgroundOpacity());

        String document = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(document.contains("\"schemaVersion\": 2"));
        assertTrue(document.contains("\"worldNameEnabled\": true"));
        assertTrue(document.contains("\"worldNameDisplay\": \"IN_FRONT\""));
        assertTrue(document.contains("\"viewDirectionEnabled\": true"));
        assertTrue(document.contains("\"viewDirectionDisplay\": \"WITH_DETAILS\""));
        assertTrue(document.contains("\"coordinateLensEnabled\": true"));
        assertTrue(document.contains("\"coordinateCopyFormat\": \"CMI_TPPOS\""));
        assertTrue(document.contains("\"biomeThemeOverrideEnabled\": true"));
        assertTrue(document.contains("\"biomeTransitionEnabled\": true"));
        assertTrue(document.contains("\"movementSpeedEnabled\": true"));
        assertTrue(document.contains("\"targetLingerEnabled\": true"));
        assertTrue(document.contains("\"targetNameMode\": \"FRIENDLY\""));
    }

    @Test
    void migratesTheExistingUnversionedSettingsAndLegacyFields() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        Files.writeString(path, readFixture("legacy-unversioned.json"), StandardCharsets.UTF_8);

        LocatorHudConfigStore store = new LocatorHudConfigStore(path);
        LocatorHudConfigStore.LoadResult migrated = store.load();

        assertSame(LocatorHudConfigStore.LoadStatus.MIGRATED, migrated.status());
        assertTrue(migrated.requiresWriteBack());
        assertFalse(migrated.settings().enabled());
        assertTrue(migrated.settings().mainPanelEnabled());
        assertTrue(migrated.settings().viewDirectionEnabled());
        assertSame(ViewDirectionDisplay.ON, migrated.settings().viewDirectionDisplay());
        assertFalse(migrated.settings().coordinateLensEnabled());
        assertFalse(migrated.settings().biomeTransitionEnabled());
        assertFalse(migrated.settings().movementSpeedEnabled());
        assertFalse(migrated.settings().targetLingerEnabled());
        assertSame(TargetNameMode.API_ACCURATE, migrated.settings().targetNameMode());
        assertSame(CoordinateDisplayMode.BLOCK_ONLY, migrated.settings().coordinateDisplay());
        assertSame(CoordinatePrecision.ONE_DECIMAL, migrated.settings().precision());
        assertSame(WorldNameDisplay.OFF, migrated.settings().worldNameDisplay());
        assertSame(HudScale.SMALL, migrated.settings().hudScale());
        assertSame(HudScale.EXTRA_SMALL, migrated.settings().detailsHudScale());
        assertSame(ColorPalette.DUO_TONE, migrated.settings().palette());
        assertSame(BackgroundOpacity.SOFT, migrated.settings().backgroundOpacity());
        assertSame(BackgroundOpacity.STRONG, migrated.settings().detailsBackgroundOpacity());

        assertTrue(store.save(migrated.settings()).wasSaved());
        assertSame(LocatorHudConfigStore.LoadStatus.LOADED, store.load().status());
    }

    @Test
    void migratesTheSchemaOneDirectionBooleanToTheThreeStateSetting() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        Files.writeString(path, readFixture("schema-v1-direction-off.json"), StandardCharsets.UTF_8);

        LocatorHudConfigStore store = new LocatorHudConfigStore(path);
        LocatorHudConfigStore.LoadResult migrated = store.load();

        assertSame(LocatorHudConfigStore.LoadStatus.MIGRATED, migrated.status());
        assertTrue(migrated.requiresWriteBack());
        assertFalse(migrated.settings().viewDirectionEnabled());
        assertSame(ViewDirectionDisplay.OFF, migrated.settings().viewDirectionDisplay());

        assertTrue(store.save(migrated.settings()).wasSaved());
        String document = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(document.contains("\"schemaVersion\": 2"));
        assertTrue(document.contains("\"viewDirectionEnabled\": false"));
        assertTrue(document.contains("\"viewDirectionDisplay\": \"OFF\""));
    }

    @Test
    void validatesUnknownEnumValuesWithoutDiscardingTheWholeFile() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        Files.writeString(path, """
            {
              "schemaVersion": 2,
              "enabled": false,
              "corner": "NOT_A_CORNER",
              "coordinateDisplay": "NOT_A_MODE",
              "coordinateCopyFormat": "NOT_A_MODE",
              "targetNameMode": "NOT_A_MODE",
              "viewDirectionEnabled": false,
              "viewDirectionDisplay": "NOT_A_MODE",
              "palette": "NOT_A_PALETTE"
            }
            """, StandardCharsets.UTF_8);

        LocatorHudConfigStore.LoadResult result = new LocatorHudConfigStore(path).load();

        assertSame(LocatorHudConfigStore.LoadStatus.LOADED, result.status());
        assertFalse(result.settings().enabled());
        assertSame(HudCorner.TOP_LEFT, result.settings().corner());
        assertSame(CoordinateDisplayMode.DECIMAL_ONLY, result.settings().coordinateDisplay());
        assertSame(CoordinateCopyFormat.PLAIN, result.settings().coordinateCopyFormat());
        assertSame(TargetNameMode.API_ACCURATE, result.settings().targetNameMode());
        assertSame(ViewDirectionDisplay.OFF, result.settings().viewDirectionDisplay());
        assertSame(ColorPalette.OCEAN, result.settings().palette());
    }

    @Test
    void preservesMalformedJsonAsADatedBackupBeforeUsingDefaults() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        String malformed = "{ this is not json";
        Files.writeString(path, malformed, StandardCharsets.UTF_8);
        Clock clock = Clock.fixed(Instant.parse("2026-08-19T07:45:30.123Z"), ZoneOffset.UTC);

        LocatorHudConfigStore.LoadResult result = new LocatorHudConfigStore(path, clock).load();

        assertSame(LocatorHudConfigStore.LoadStatus.RECOVERED_MALFORMED, result.status());
        assertTrue(result.requiresWriteBack());
        assertFalse(result.blocksPersistence());
        assertFalse(Files.exists(path));
        assertNotNull(result.backupPath());
        assertEquals(
            "locator-hud.20260819-074530-123.broken.json",
            result.backupPath().getFileName().toString()
        );
        assertEquals(malformed, Files.readString(result.backupPath(), StandardCharsets.UTF_8));
        assertNotNull(result.message());
        assertTrue(result.settings().enabled());
    }

    @Test
    void doesNotAuthorizeOverwritingMalformedJsonWhenItCannotBeBackedUp() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        String malformed = "{ preserve me";
        Files.writeString(path, malformed, StandardCharsets.UTF_8);
        Clock clock = Clock.fixed(Instant.parse("2026-08-19T07:45:30.123Z"), ZoneOffset.UTC);
        String backupStem = "locator-hud.20260819-074530-123";
        for (int suffix = 0; suffix < 100; suffix++) {
            String disambiguator = suffix == 0 ? "" : "." + suffix;
            Files.writeString(
                this.temporaryDirectory.resolve(backupStem + disambiguator + ".broken.json"),
                "occupied",
                StandardCharsets.UTF_8
            );
        }

        LocatorHudConfigStore.LoadResult result = new LocatorHudConfigStore(path, clock).load();

        assertSame(LocatorHudConfigStore.LoadStatus.RECOVERED_MALFORMED, result.status());
        assertFalse(result.requiresWriteBack());
        assertTrue(result.blocksPersistence());
        assertNull(result.backupPath());
        assertEquals(malformed, Files.readString(path, StandardCharsets.UTF_8));
        assertTrue(result.settings().enabled());
    }

    @Test
    void doesNotOverwriteAConfigurationFromANewerSchema() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        String futureDocument = """
            {
              "schemaVersion": 99,
              "enabled": false
            }
            """;
        Files.writeString(path, futureDocument, StandardCharsets.UTF_8);

        LocatorHudConfigStore.LoadResult result = new LocatorHudConfigStore(path).load();

        assertSame(LocatorHudConfigStore.LoadStatus.UNSUPPORTED_FUTURE_SCHEMA, result.status());
        assertFalse(result.requiresWriteBack());
        assertTrue(result.blocksPersistence());
        assertTrue(result.settings().enabled());
        assertNull(result.backupPath());
        assertEquals(futureDocument, Files.readString(path, StandardCharsets.UTF_8));
    }

    @Test
    void treatsANonIntegerSchemaAsMalformedInsteadOfSilentlyTruncatingIt() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud.json");
        Files.writeString(path, "{\"schemaVersion\": 1.5}", StandardCharsets.UTF_8);

        LocatorHudConfigStore.LoadResult result = new LocatorHudConfigStore(path).load();

        assertSame(LocatorHudConfigStore.LoadStatus.RECOVERED_MALFORMED, result.status());
        assertTrue(result.requiresWriteBack());
        assertNotNull(result.backupPath());
        assertFalse(Files.exists(path));
    }

    @Test
    void reportsSaveFailuresAndKeepsTheInMemorySettings() throws IOException {
        Path parentFile = this.temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "occupied", StandardCharsets.UTF_8);
        LocatorHudConfigStore store = new LocatorHudConfigStore(parentFile.resolve("locator-hud.json"));
        LocatorHudSettings settings = LocatorHudSettings.defaults();
        settings.setEnabled(false);

        LocatorHudConfigStore.SaveResult result = store.save(settings);

        assertSame(LocatorHudConfigStore.SaveStatus.FAILED, result.status());
        assertNotNull(result.message());
        assertFalse(settings.enabled());
    }

    private static String readFixture(String fileName) throws IOException {
        String resourcePath = "/config/" + fileName;
        try (InputStream input = LocatorHudConfigStoreTest.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Missing test fixture: " + resourcePath);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
