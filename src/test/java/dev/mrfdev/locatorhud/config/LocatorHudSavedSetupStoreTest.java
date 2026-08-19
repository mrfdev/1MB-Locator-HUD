package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.PanelWidth;
import dev.mrfdev.locatorhud.PanelWidthLimits;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class LocatorHudSavedSetupStoreTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void missingFileMeansTheSingleSlotIsEmpty() {
        LocatorHudSavedSetupStore.LoadResult result = store().load();

        assertSame(LocatorHudConfigStore.LoadStatus.MISSING, result.status());
        assertTrue(result.settings().isEmpty());
        assertFalse(result.requiresWriteBack());
    }

    @Test
    void savesAndLoadsOneIndependentSettingsSnapshot() {
        LocatorHudSavedSetupStore store = store();
        LocatorHudSettings settings = LocatorHudSettings.defaults();
        settings.setCoordinateDisplay(CoordinateDisplayMode.BOTH);
        settings.setBiomeEnabled(true);
        settings.setBiomeThemeOverrideEnabled(true);
        settings.setAccessibilitySettingsEnabled(true);
        settings.setHudScale(HudScale.HUGE);
        settings.setMainPanelMinimumWidth(PanelWidth.PX_160);
        settings.setMainPanelMaximumWidth(PanelWidth.PX_280);

        assertTrue(store.save(settings).wasSaved());
        settings.setBiomeEnabled(false);
        LocatorHudSavedSetupStore.LoadResult loaded = store.load();

        assertSame(LocatorHudConfigStore.LoadStatus.LOADED, loaded.status());
        assertTrue(loaded.settings().isPresent());
        assertSame(
            CoordinateDisplayMode.BOTH,
            loaded.settings().orElseThrow().coordinateDisplay()
        );
        assertTrue(loaded.settings().orElseThrow().biomeEnabled());
        assertTrue(loaded.settings().orElseThrow().biomeThemeOverrideEnabled());
        assertTrue(loaded.settings().orElseThrow().accessibilitySettingsEnabled());
        assertSame(HudScale.HUGE, loaded.settings().orElseThrow().hudScale());
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_160, PanelWidth.PX_280),
            loaded.settings().orElseThrow().mainPanelWidthLimits()
        );
        assertEquals(
            this.temporaryDirectory.resolve("locator-hud-saved-setup.json"),
            store.path()
        );
    }

    @Test
    void malformedSlotIsPreservedAndNeverRestoredAsDefaults() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud-saved-setup.json");
        Files.writeString(path, "{ invalid", StandardCharsets.UTF_8);
        Clock clock = Clock.fixed(Instant.parse("2026-08-19T18:50:00Z"), ZoneOffset.UTC);
        LocatorHudSavedSetupStore store = new LocatorHudSavedSetupStore(
            new LocatorHudConfigStore(path, clock)
        );

        LocatorHudSavedSetupStore.LoadResult result = store.load();

        assertSame(LocatorHudConfigStore.LoadStatus.RECOVERED_MALFORMED, result.status());
        assertTrue(result.settings().isEmpty());
        assertNotNull(result.backupPath());
        assertTrue(Files.isRegularFile(result.backupPath()));
        assertFalse(Files.exists(path));
    }

    @Test
    void savingAgainReplacesTheOneSlotInsteadOfCreatingProfiles() {
        LocatorHudSavedSetupStore store = store();
        LocatorHudSettings first = LocatorHudSettings.defaults();
        first.setCoordinateDisplay(CoordinateDisplayMode.BOTH);
        LocatorHudSettings replacement = LocatorHudSettings.defaults();
        replacement.setCoordinateDisplay(CoordinateDisplayMode.HIDDEN);

        assertTrue(store.save(first).wasSaved());
        assertTrue(store.save(replacement).wasSaved());

        LocatorHudSettings loaded = store.load().settings().orElseThrow();
        assertSame(CoordinateDisplayMode.HIDDEN, loaded.coordinateDisplay());
        assertEquals(1, countSavedSetupFiles());
    }

    @Test
    void futureSchemaSlotRemainsUnavailableAndProtectedFromDowngradeWrites() throws IOException {
        Path path = this.temporaryDirectory.resolve("locator-hud-saved-setup.json");
        String futureDocument = """
            {
              "schemaVersion": %d,
              "coordinateDisplay": "BOTH"
            }
            """.formatted(LocatorHudConfigStore.CURRENT_SCHEMA_VERSION + 1);
        Files.writeString(path, futureDocument, StandardCharsets.UTF_8);
        LocatorHudSavedSetupStore store = store();

        LocatorHudSavedSetupStore.LoadResult loaded = store.load();
        LocatorHudConfigStore.SaveResult saveResult = store.save(LocatorHudSettings.defaults());

        assertSame(
            LocatorHudConfigStore.LoadStatus.UNSUPPORTED_FUTURE_SCHEMA,
            loaded.status()
        );
        assertTrue(loaded.settings().isEmpty());
        assertSame(
            LocatorHudConfigStore.SaveStatus.BLOCKED_PROTECTED_CONFIG,
            saveResult.status()
        );
        assertEquals(futureDocument, Files.readString(path, StandardCharsets.UTF_8));
    }

    private LocatorHudSavedSetupStore store() {
        return new LocatorHudSavedSetupStore(
            this.temporaryDirectory.resolve("locator-hud-saved-setup.json")
        );
    }

    private long countSavedSetupFiles() {
        try (var files = Files.list(this.temporaryDirectory)) {
            return files.filter(path -> path.getFileName().toString().equals(
                "locator-hud-saved-setup.json"
            )).count();
        } catch (IOException exception) {
            throw new AssertionError(exception);
        }
    }
}
