package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewAnglePrecision;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import java.util.Objects;

public enum LocatorHudPreset {
    MINIMAL("value.locatorhud.preset.minimal"),
    EXPLORER("value.locatorhud.preset.explorer"),
    BUILDER("value.locatorhud.preset.builder"),
    PRIVACY("value.locatorhud.preset.privacy");

    private final String translationKey;

    LocatorHudPreset(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public LocatorHudSettings applyTo(LocatorHudSettings current) {
        LocatorHudSettings settings = Objects.requireNonNull(current, "current").copy();
        settings.setEnabled(true);
        switch (this) {
            case MINIMAL -> applyMinimal(settings);
            case EXPLORER -> applyExplorer(settings);
            case BUILDER -> applyBuilder(settings);
            case PRIVACY -> applyPrivacy(settings);
        }
        settings.validate();
        return settings;
    }

    private static void applyMinimal(LocatorHudSettings settings) {
        applyContentDefaults(settings);
        settings.setDetailsPanelEnabled(false);
        settings.setCoordinateDisplay(CoordinateDisplayMode.DECIMAL_ONLY);
        settings.setPrecision(CoordinatePrecision.NONE);
        settings.setWorldNameDisplay(WorldNameDisplay.OFF);
        settings.setViewDirectionDisplay(ViewDirectionDisplay.ON);
        settings.setHudScale(HudScale.COMPACT);
        settings.setBackgroundOpacity(BackgroundOpacity.OFF);
        settings.setDetailsHudScale(HudScale.COMPACT);
        settings.setDetailsBackgroundOpacity(BackgroundOpacity.OFF);
    }

    private static void applyExplorer(LocatorHudSettings settings) {
        applyContentDefaults(settings);
        settings.setCoordinateDisplay(CoordinateDisplayMode.DECIMAL_ONLY);
        settings.setPrecision(CoordinatePrecision.ONE_DECIMAL);
        settings.setCoordinateLensEnabled(true);
        settings.setWorldNameDisplay(WorldNameDisplay.BEHIND);
        settings.setViewDirectionDisplay(ViewDirectionDisplay.WITH_DETAILS);
        settings.setViewAnglesEnabled(true);
        settings.setBiomeEnabled(true);
        settings.setBiomeTransitionEnabled(true);
        settings.setMovementSpeedEnabled(true);
        settings.setTargetNameMode(TargetNameMode.FRIENDLY);
        settings.setHudScale(HudScale.NORMAL);
        settings.setBackgroundOpacity(BackgroundOpacity.BALANCED);
        settings.setDetailsHudScale(HudScale.COMPACT);
        settings.setDetailsBackgroundOpacity(BackgroundOpacity.FAINT);
    }

    private static void applyBuilder(LocatorHudSettings settings) {
        applyContentDefaults(settings);
        settings.setCoordinateDisplay(CoordinateDisplayMode.BOTH);
        settings.setPrecision(CoordinatePrecision.TWO_DECIMALS);
        settings.setWorldNameDisplay(WorldNameDisplay.OFF);
        settings.setViewDirectionDisplay(ViewDirectionDisplay.WITH_DETAILS);
        settings.setViewAnglesEnabled(true);
        settings.setTargetBlockEnabled(true);
        settings.setTargetFluidEnabled(true);
        settings.setTargetEntityEnabled(true);
        settings.setTargetNameMode(TargetNameMode.FRIENDLY);
        settings.setAutoHideEmptyTargetValues(true);
        settings.setTargetLingerEnabled(true);
        settings.setHudScale(HudScale.NORMAL);
        settings.setBackgroundOpacity(BackgroundOpacity.BALANCED);
        settings.setDetailsHudScale(HudScale.COMPACT);
        settings.setDetailsBackgroundOpacity(BackgroundOpacity.SOFT);
    }

    private static void applyPrivacy(LocatorHudSettings settings) {
        applyContentDefaults(settings);
        settings.setCoordinateDisplay(CoordinateDisplayMode.HIDDEN);
        settings.setPrecision(CoordinatePrecision.NONE);
        settings.setWorldNameDisplay(WorldNameDisplay.OFF);
        settings.setViewDirectionDisplay(ViewDirectionDisplay.ON);
        settings.setBiomeEnabled(true);
        settings.setTargetNameMode(TargetNameMode.FRIENDLY);
        settings.setAutoHideEmptyTargetValues(true);
        settings.setHudScale(HudScale.COMPACT);
        settings.setBackgroundOpacity(BackgroundOpacity.OFF);
        settings.setDetailsHudScale(HudScale.COMPACT);
        settings.setDetailsBackgroundOpacity(BackgroundOpacity.OFF);
    }

    private static void applyContentDefaults(LocatorHudSettings settings) {
        settings.setMainPanelEnabled(true);
        settings.setDetailsPanelEnabled(true);
        settings.setCoordinateLensEnabled(false);
        settings.setViewAnglesEnabled(false);
        settings.setViewAnglePrecision(ViewAnglePrecision.WHOLE);
        settings.setBiomeEnabled(false);
        settings.setBiomeTransitionEnabled(false);
        settings.setMovementSpeedEnabled(false);
        settings.setTargetBlockEnabled(false);
        settings.setTargetFluidEnabled(false);
        settings.setTargetEntityEnabled(false);
        settings.setTargetNameMode(TargetNameMode.API_ACCURATE);
        settings.setAutoHideEmptyTargetValues(false);
        settings.setTargetLingerEnabled(false);
    }
}
