package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewAnglePrecision;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocatorHudConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("locatorhud");

    private final LocatorHudConfigStore store;
    private final LocatorHudSavedSetupStore savedSetupStore;
    private final LocatorHudSettings settings;
    private final LocatorHudConfigStore.LoadStatus loadStatus;
    private final Path recoveredBackup;
    private String persistenceBlockReason;
    private LocatorHudConfigStore.SaveResult lastSaveResult = LocatorHudConfigStore.SaveResult.saved();
    private LocatorHudConfigStore.SaveResult lastSavedSetupSaveResult =
        LocatorHudConfigStore.SaveResult.saved();
    private LocatorHudSettings savedSetup;

    private LocatorHudConfig(
        LocatorHudConfigStore store,
        LocatorHudConfigStore.LoadResult loadResult,
        LocatorHudSavedSetupStore savedSetupStore,
        LocatorHudSavedSetupStore.LoadResult savedSetupLoadResult
    ) {
        this.store = store;
        this.savedSetupStore = savedSetupStore;
        this.settings = loadResult.settings();
        this.savedSetup = savedSetupLoadResult.settings().orElse(null);
        this.loadStatus = loadResult.status();
        this.recoveredBackup = loadResult.backupPath();
        if (loadResult.blocksPersistence()) {
            this.persistenceBlockReason = loadResult.status()
                == LocatorHudConfigStore.LoadStatus.UNSUPPORTED_FUTURE_SCHEMA
                    ? loadResult.message()
                    : "The invalid configuration could not be backed up; use Reset to replace it explicitly";
            this.lastSaveResult = LocatorHudConfigStore.SaveResult.blocked(this.persistenceBlockReason);
        }
    }

    public static LocatorHudConfig load() {
        Path configDirectory = FabricLoader.getInstance().getConfigDir();
        Path path = configDirectory.resolve("locator-hud.json");
        LocatorHudConfigStore store = new LocatorHudConfigStore(path);
        LocatorHudConfigStore.LoadResult loadResult = store.load();
        LocatorHudSavedSetupStore savedSetupStore = new LocatorHudSavedSetupStore(
            configDirectory.resolve("locator-hud-saved-setup.json")
        );
        LocatorHudSavedSetupStore.LoadResult savedSetupLoadResult = savedSetupStore.load();
        LocatorHudConfig config = new LocatorHudConfig(
            store,
            loadResult,
            savedSetupStore,
            savedSetupLoadResult
        );
        config.reportLoadResult(loadResult);
        config.reportSavedSetupLoadResult(savedSetupLoadResult);
        if (loadResult.requiresWriteBack()) {
            config.save();
        }
        if (savedSetupLoadResult.requiresWriteBack()) {
            LocatorHudSettings migratedSavedSetup = savedSetupLoadResult.settings().orElseThrow();
            config.lastSavedSetupSaveResult = savedSetupStore.save(migratedSavedSetup);
        }
        return config;
    }

    public LocatorHudConfigStore.SaveResult save() {
        this.settings.validate();
        if (this.persistenceBlockReason != null) {
            this.lastSaveResult = LocatorHudConfigStore.SaveResult.blocked(this.persistenceBlockReason);
            return this.lastSaveResult;
        }

        LocatorHudConfigStore.SaveResult previousResult = this.lastSaveResult;
        this.lastSaveResult = this.store.save(this.settings);
        if (!this.lastSaveResult.wasSaved()
            && previousResult.status() != LocatorHudConfigStore.SaveStatus.FAILED) {
            LOGGER.warn(
                "Locator HUD settings could not be saved to {}: {}",
                this.store.path(),
                this.lastSaveResult.message()
            );
        }
        return this.lastSaveResult;
    }

    public void reset() {
        this.persistenceBlockReason = null;
        this.settings.replaceWith(LocatorHudSettings.defaults());
        save();
    }

    public LocatorHudSettings snapshot() {
        return this.settings.copy();
    }

    public void applySettings(LocatorHudSettings replacement) {
        this.settings.replaceWith(replacement);
        save();
    }

    public void applyPreset(LocatorHudPreset preset) {
        applySettings(Objects.requireNonNull(preset, "preset").applyTo(this.settings));
    }

    public boolean hasSavedSetup() {
        return this.savedSetup != null;
    }

    public LocatorHudConfigStore.SaveResult saveCurrentSetup() {
        LocatorHudSettings snapshot = this.settings.copy();
        this.lastSavedSetupSaveResult = this.savedSetupStore.save(snapshot);
        if (this.lastSavedSetupSaveResult.wasSaved()) {
            this.savedSetup = snapshot;
        } else {
            LOGGER.warn(
                "Locator HUD saved setup could not be written to {}: {}",
                this.savedSetupStore.path(),
                this.lastSavedSetupSaveResult.message()
            );
        }
        return this.lastSavedSetupSaveResult;
    }

    public boolean restoreSavedSetup() {
        if (this.savedSetup == null) {
            return false;
        }
        applySettings(this.savedSetup);
        return true;
    }

    public LocatorHudConfigStore.SaveResult lastSavedSetupSaveResult() {
        return this.lastSavedSetupSaveResult;
    }

    public LocatorHudConfigStore.LoadStatus loadStatus() {
        return this.loadStatus;
    }

    public Path recoveredBackup() {
        return this.recoveredBackup;
    }

    public LocatorHudConfigStore.SaveResult lastSaveResult() {
        return this.lastSaveResult;
    }

    public boolean enabled() {
        return this.settings.enabled();
    }

    public void setEnabled(boolean enabled) {
        update(settings -> settings.setEnabled(enabled));
    }

    public void toggleEnabled() {
        setEnabled(!enabled());
    }

    public boolean mainPanelEnabled() {
        return this.settings.mainPanelEnabled();
    }

    public void setMainPanelEnabled(boolean mainPanelEnabled) {
        update(settings -> settings.setMainPanelEnabled(mainPanelEnabled));
    }

    public boolean detailsPanelEnabled() {
        return this.settings.detailsPanelEnabled();
    }

    public void setDetailsPanelEnabled(boolean detailsPanelEnabled) {
        update(settings -> settings.setDetailsPanelEnabled(detailsPanelEnabled));
    }

    public HudCorner corner() {
        return this.settings.corner();
    }

    public void setCorner(HudCorner corner) {
        update(settings -> settings.setCorner(corner));
    }

    public CoordinateDisplayMode coordinateDisplay() {
        return this.settings.coordinateDisplay();
    }

    public void setCoordinateDisplay(CoordinateDisplayMode coordinateDisplay) {
        update(settings -> settings.setCoordinateDisplay(coordinateDisplay));
    }

    public CoordinatePrecision precision() {
        return this.settings.precision();
    }

    public void setPrecision(CoordinatePrecision precision) {
        update(settings -> settings.setPrecision(precision));
    }

    public boolean coordinateLensEnabled() {
        return this.settings.coordinateLensEnabled();
    }

    public void setCoordinateLensEnabled(boolean coordinateLensEnabled) {
        update(settings -> settings.setCoordinateLensEnabled(coordinateLensEnabled));
    }

    public CoordinateCopyFormat coordinateCopyFormat() {
        return this.settings.coordinateCopyFormat();
    }

    public void setCoordinateCopyFormat(CoordinateCopyFormat coordinateCopyFormat) {
        update(settings -> settings.setCoordinateCopyFormat(coordinateCopyFormat));
    }

    public boolean worldNameEnabled() {
        return this.settings.worldNameEnabled();
    }

    public void setWorldNameEnabled(boolean worldNameEnabled) {
        update(settings -> settings.setWorldNameEnabled(worldNameEnabled));
    }

    public WorldNameDisplay worldNameDisplay() {
        return this.settings.worldNameDisplay();
    }

    public void setWorldNameDisplay(WorldNameDisplay worldNameDisplay) {
        update(settings -> settings.setWorldNameDisplay(worldNameDisplay));
    }

    public boolean viewDirectionEnabled() {
        return this.settings.viewDirectionEnabled();
    }

    public void setViewDirectionEnabled(boolean viewDirectionEnabled) {
        update(settings -> settings.setViewDirectionEnabled(viewDirectionEnabled));
    }

    public ViewDirectionDisplay viewDirectionDisplay() {
        return this.settings.viewDirectionDisplay();
    }

    public void setViewDirectionDisplay(ViewDirectionDisplay viewDirectionDisplay) {
        update(settings -> settings.setViewDirectionDisplay(viewDirectionDisplay));
    }

    public boolean viewAnglesEnabled() {
        return this.settings.viewAnglesEnabled();
    }

    public void setViewAnglesEnabled(boolean viewAnglesEnabled) {
        update(settings -> settings.setViewAnglesEnabled(viewAnglesEnabled));
    }

    public ViewAnglePrecision viewAnglePrecision() {
        return this.settings.viewAnglePrecision();
    }

    public void setViewAnglePrecision(ViewAnglePrecision viewAnglePrecision) {
        update(settings -> settings.setViewAnglePrecision(viewAnglePrecision));
    }

    public boolean biomeEnabled() {
        return this.settings.biomeEnabled();
    }

    public void setBiomeEnabled(boolean biomeEnabled) {
        update(settings -> settings.setBiomeEnabled(biomeEnabled));
    }

    public boolean biomeTransitionEnabled() {
        return this.settings.biomeTransitionEnabled();
    }

    public void setBiomeTransitionEnabled(boolean biomeTransitionEnabled) {
        update(settings -> settings.setBiomeTransitionEnabled(biomeTransitionEnabled));
    }

    public boolean movementSpeedEnabled() {
        return this.settings.movementSpeedEnabled();
    }

    public void setMovementSpeedEnabled(boolean movementSpeedEnabled) {
        update(settings -> settings.setMovementSpeedEnabled(movementSpeedEnabled));
    }

    public boolean targetBlockEnabled() {
        return this.settings.targetBlockEnabled();
    }

    public void setTargetBlockEnabled(boolean targetBlockEnabled) {
        update(settings -> settings.setTargetBlockEnabled(targetBlockEnabled));
    }

    public boolean targetFluidEnabled() {
        return this.settings.targetFluidEnabled();
    }

    public void setTargetFluidEnabled(boolean targetFluidEnabled) {
        update(settings -> settings.setTargetFluidEnabled(targetFluidEnabled));
    }

    public boolean targetEntityEnabled() {
        return this.settings.targetEntityEnabled();
    }

    public void setTargetEntityEnabled(boolean targetEntityEnabled) {
        update(settings -> settings.setTargetEntityEnabled(targetEntityEnabled));
    }

    public TargetNameMode targetNameMode() {
        return this.settings.targetNameMode();
    }

    public void setTargetNameMode(TargetNameMode targetNameMode) {
        update(settings -> settings.setTargetNameMode(targetNameMode));
    }

    public boolean autoHideEmptyTargetValues() {
        return this.settings.autoHideEmptyTargetValues();
    }

    public void setAutoHideEmptyTargetValues(boolean autoHideEmptyTargetValues) {
        update(settings -> settings.setAutoHideEmptyTargetValues(autoHideEmptyTargetValues));
    }

    public boolean targetLingerEnabled() {
        return this.settings.targetLingerEnabled();
    }

    public void setTargetLingerEnabled(boolean targetLingerEnabled) {
        update(settings -> settings.setTargetLingerEnabled(targetLingerEnabled));
    }

    public HudScale hudScale() {
        return this.settings.hudScale();
    }

    public void setHudScale(HudScale hudScale) {
        update(settings -> settings.setHudScale(hudScale));
    }

    public HudCorner detailsCorner() {
        return this.settings.detailsCorner();
    }

    public void setDetailsCorner(HudCorner detailsCorner) {
        update(settings -> settings.setDetailsCorner(detailsCorner));
    }

    public HudScale detailsHudScale() {
        return this.settings.detailsHudScale();
    }

    public void setDetailsHudScale(HudScale detailsHudScale) {
        update(settings -> settings.setDetailsHudScale(detailsHudScale));
    }

    public ColorPalette palette() {
        return this.settings.palette();
    }

    public void setPalette(ColorPalette palette) {
        update(settings -> settings.setPalette(palette));
    }

    public boolean biomeThemeOverrideEnabled() {
        return this.settings.biomeThemeOverrideEnabled();
    }

    public void setBiomeThemeOverrideEnabled(boolean biomeThemeOverrideEnabled) {
        update(settings -> settings.setBiomeThemeOverrideEnabled(biomeThemeOverrideEnabled));
    }

    public BackgroundOpacity backgroundOpacity() {
        return this.settings.backgroundOpacity();
    }

    public void setBackgroundOpacity(BackgroundOpacity backgroundOpacity) {
        update(settings -> settings.setBackgroundOpacity(backgroundOpacity));
    }

    public BackgroundOpacity detailsBackgroundOpacity() {
        return this.settings.detailsBackgroundOpacity();
    }

    public void setDetailsBackgroundOpacity(BackgroundOpacity detailsBackgroundOpacity) {
        update(settings -> settings.setDetailsBackgroundOpacity(detailsBackgroundOpacity));
    }

    public boolean panelShadow() {
        return this.settings.panelShadow();
    }

    public void setPanelShadow(boolean panelShadow) {
        update(settings -> settings.setPanelShadow(panelShadow));
    }

    public boolean textShadow() {
        return this.settings.textShadow();
    }

    public void setTextShadow(boolean textShadow) {
        update(settings -> settings.setTextShadow(textShadow));
    }

    private void update(Consumer<LocatorHudSettings> change) {
        change.accept(this.settings);
        save();
    }

    private void reportLoadResult(LocatorHudConfigStore.LoadResult result) {
        switch (result.status()) {
            case LOADED, MISSING -> {
            }
            case MIGRATED -> LOGGER.info(
                "Migrated Locator HUD settings at {} to schema {}",
                this.store.path(),
                LocatorHudConfigStore.CURRENT_SCHEMA_VERSION
            );
            case RECOVERED_MALFORMED -> {
                if (result.backupPath() != null) {
                    LOGGER.warn(
                        "Locator HUD settings at {} were invalid and defaults were loaded. Backup: {}. Cause: {}",
                        this.store.path(),
                        result.backupPath(),
                        result.message()
                    );
                } else {
                    LOGGER.warn(
                        "Locator HUD settings at {} were invalid, but no backup could be created. "
                            + "Defaults are active and persistence is blocked until Reset is used. Cause: {}",
                        this.store.path(),
                        result.message()
                    );
                }
            }
            case UNSUPPORTED_FUTURE_SCHEMA -> LOGGER.warn(
                "Locator HUD settings at {} use a newer schema. Defaults are active for this session "
                    + "and persistence is blocked until Reset is used: {}",
                this.store.path(),
                result.message()
            );
        }
    }

    private void reportSavedSetupLoadResult(LocatorHudSavedSetupStore.LoadResult result) {
        switch (result.status()) {
            case LOADED, MISSING -> {
            }
            case MIGRATED -> LOGGER.info(
                "Migrated Locator HUD saved setup at {} to schema {}",
                this.savedSetupStore.path(),
                LocatorHudConfigStore.CURRENT_SCHEMA_VERSION
            );
            case RECOVERED_MALFORMED -> LOGGER.warn(
                "Locator HUD saved setup at {} was invalid and is unavailable. Backup: {}. Cause: {}",
                this.savedSetupStore.path(),
                result.backupPath(),
                result.message()
            );
            case UNSUPPORTED_FUTURE_SCHEMA -> LOGGER.warn(
                "Locator HUD saved setup at {} uses a newer schema and is unavailable: {}",
                this.savedSetupStore.path(),
                result.message()
            );
        }
    }
}
