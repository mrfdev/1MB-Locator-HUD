package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import org.junit.jupiter.api.Test;

final class LocatorHudPresetTest {
    @Test
    void minimalKeepsOnlyACompactMainNavigationPanel() {
        LocatorHudSettings settings = allFeaturesEnabled();
        LocatorHudSettings result = LocatorHudPreset.MINIMAL.applyTo(settings);

        assertTrue(result.enabled());
        assertTrue(result.mainPanelEnabled());
        assertFalse(result.detailsPanelEnabled());
        assertSame(CoordinateDisplayMode.DECIMAL_ONLY, result.coordinateDisplay());
        assertSame(CoordinatePrecision.NONE, result.precision());
        assertSame(WorldNameDisplay.OFF, result.worldNameDisplay());
        assertSame(ViewDirectionDisplay.ON, result.viewDirectionDisplay());
        assertFalse(result.viewAnglesEnabled());
        assertSame(HudScale.COMPACT, result.hudScale());
        assertSame(BackgroundOpacity.OFF, result.backgroundOpacity());
    }

    @Test
    void explorerEnablesNavigationAndLocalEnvironmentRows() {
        LocatorHudSettings result = LocatorHudPreset.EXPLORER.applyTo(
            LocatorHudSettings.defaults()
        );

        assertTrue(result.coordinateLensEnabled());
        assertSame(WorldNameDisplay.BEHIND, result.worldNameDisplay());
        assertSame(ViewDirectionDisplay.WITH_DETAILS, result.viewDirectionDisplay());
        assertTrue(result.viewAnglesEnabled());
        assertTrue(result.biomeEnabled());
        assertTrue(result.biomeTransitionEnabled());
        assertTrue(result.movementSpeedEnabled());
        assertFalse(result.targetBlockEnabled());
        assertSame(TargetNameMode.FRIENDLY, result.targetNameMode());
        assertSame(BackgroundOpacity.FAINT, result.detailsBackgroundOpacity());
    }

    @Test
    void builderEnablesBothCoordinatesAndAutoHiddenFriendlyTargets() {
        LocatorHudSettings result = LocatorHudPreset.BUILDER.applyTo(
            LocatorHudSettings.defaults()
        );

        assertSame(CoordinateDisplayMode.BOTH, result.coordinateDisplay());
        assertSame(CoordinatePrecision.TWO_DECIMALS, result.precision());
        assertTrue(result.targetBlockEnabled());
        assertTrue(result.targetFluidEnabled());
        assertTrue(result.targetEntityEnabled());
        assertTrue(result.autoHideEmptyTargetValues());
        assertTrue(result.targetLingerEnabled());
        assertSame(TargetNameMode.FRIENDLY, result.targetNameMode());
        assertSame(BackgroundOpacity.SOFT, result.detailsBackgroundOpacity());
    }

    @Test
    void privacyHidesExactLocationRowsWithoutDynamicMasking() {
        LocatorHudSettings result = LocatorHudPreset.PRIVACY.applyTo(allFeaturesEnabled());

        assertTrue(result.mainPanelEnabled());
        assertTrue(result.detailsPanelEnabled());
        assertSame(CoordinateDisplayMode.HIDDEN, result.coordinateDisplay());
        assertFalse(result.coordinateLensEnabled());
        assertSame(WorldNameDisplay.OFF, result.worldNameDisplay());
        assertSame(ViewDirectionDisplay.ON, result.viewDirectionDisplay());
        assertFalse(result.viewAnglesEnabled());
        assertTrue(result.biomeEnabled());
        assertFalse(result.movementSpeedEnabled());
        assertFalse(result.targetBlockEnabled());
        assertSame(BackgroundOpacity.OFF, result.backgroundOpacity());
        assertSame(BackgroundOpacity.OFF, result.detailsBackgroundOpacity());
    }

    @Test
    void presetsPreserveThemePositionsShadowsAndCopyFormat() {
        LocatorHudSettings settings = LocatorHudSettings.defaults();
        settings.setCorner(HudCorner.BOTTOM_RIGHT);
        settings.setDetailsCorner(HudCorner.BOTTOM_LEFT);
        settings.setPalette(ColorPalette.GOLD);
        settings.setBiomeThemeOverrideEnabled(true);
        settings.setTextShadow(false);
        settings.setPanelShadow(false);
        settings.setCoordinateCopyFormat(CoordinateCopyFormat.CMI_TPPOS);

        LocatorHudSettings result = LocatorHudPreset.MINIMAL.applyTo(settings);

        assertSame(HudCorner.BOTTOM_RIGHT, result.corner());
        assertSame(HudCorner.BOTTOM_LEFT, result.detailsCorner());
        assertSame(ColorPalette.GOLD, result.palette());
        assertTrue(result.biomeThemeOverrideEnabled());
        assertFalse(result.textShadow());
        assertFalse(result.panelShadow());
        assertSame(CoordinateCopyFormat.CMI_TPPOS, result.coordinateCopyFormat());
    }

    private static LocatorHudSettings allFeaturesEnabled() {
        LocatorHudSettings settings = LocatorHudSettings.defaults();
        settings.setCoordinateLensEnabled(true);
        settings.setViewDirectionDisplay(ViewDirectionDisplay.WITH_DETAILS);
        settings.setViewAnglesEnabled(true);
        settings.setBiomeEnabled(true);
        settings.setBiomeTransitionEnabled(true);
        settings.setMovementSpeedEnabled(true);
        settings.setTargetBlockEnabled(true);
        settings.setTargetFluidEnabled(true);
        settings.setTargetEntityEnabled(true);
        settings.setAutoHideEmptyTargetValues(true);
        settings.setTargetLingerEnabled(true);
        return settings;
    }
}
