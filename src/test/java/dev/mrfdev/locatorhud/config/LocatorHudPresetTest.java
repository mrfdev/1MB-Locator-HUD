package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.PanelGeometry.Offset;
import dev.mrfdev.locatorhud.PanelWidth;
import dev.mrfdev.locatorhud.PanelWidthLimits;
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
        settings.setMainPanelPlacement(HudCorner.BOTTOM_RIGHT, new Offset(-16, 24));
        settings.setDetailsPanelPlacement(HudCorner.BOTTOM_LEFT, new Offset(20, -12));
        settings.setPalette(ColorPalette.GOLD);
        settings.setAccessibilitySettingsEnabled(true);
        settings.setBiomeThemeOverrideEnabled(true);
        settings.setTextShadow(false);
        settings.setPanelShadow(false);
        settings.setCoordinateCopyFormat(CoordinateCopyFormat.CMI_TPPOS);
        settings.setMainPanelMinimumWidth(PanelWidth.PX_160);
        settings.setMainPanelMaximumWidth(PanelWidth.PX_280);
        settings.setDetailsPanelMinimumWidth(PanelWidth.PX_120);
        settings.setDetailsPanelMaximumWidth(PanelWidth.PX_240);

        LocatorHudSettings result = LocatorHudPreset.MINIMAL.applyTo(settings);

        assertSame(HudCorner.BOTTOM_RIGHT, result.corner());
        assertEquals(new Offset(-16, 24), result.mainPanelOffset());
        assertSame(HudCorner.BOTTOM_LEFT, result.detailsCorner());
        assertEquals(new Offset(20, -12), result.detailsPanelOffset());
        assertSame(ColorPalette.GOLD, result.palette());
        assertTrue(result.accessibilitySettingsEnabled());
        assertTrue(result.biomeThemeOverrideEnabled());
        assertFalse(result.textShadow());
        assertFalse(result.panelShadow());
        assertSame(CoordinateCopyFormat.CMI_TPPOS, result.coordinateCopyFormat());
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_160, PanelWidth.PX_280),
            result.mainPanelWidthLimits()
        );
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_120, PanelWidth.PX_240),
            result.detailsPanelWidthLimits()
        );
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
