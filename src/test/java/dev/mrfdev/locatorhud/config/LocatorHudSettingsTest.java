package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.PanelGeometry.Offset;
import dev.mrfdev.locatorhud.PanelWidth;
import dev.mrfdev.locatorhud.PanelWidthLimits;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import org.junit.jupiter.api.Test;

final class LocatorHudSettingsTest {
    @Test
    void copiesSettingsWithoutSharingSubsequentMutations() {
        LocatorHudSettings original = LocatorHudSettings.defaults();
        LocatorHudSettings copy = original.copy();

        copy.setEnabled(false);
        copy.setAccessibilitySettingsEnabled(true);
        copy.setHudScale(HudScale.HUGE);
        copy.setDetailsHudScale(HudScale.EXTRA_LARGE);
        copy.setCoordinateDisplay(CoordinateDisplayMode.BOTH);
        copy.setCoordinateLensEnabled(true);
        copy.setCoordinateCopyFormat(CoordinateCopyFormat.CMI_TPPOS);
        copy.setBiomeThemeOverrideEnabled(true);
        copy.setBiomeTransitionEnabled(true);
        copy.setMovementSpeedEnabled(true);
        copy.setTargetLingerEnabled(true);
        copy.setTargetNameMode(TargetNameMode.FRIENDLY);
        copy.setViewDirectionDisplay(ViewDirectionDisplay.WITH_DETAILS);
        copy.setMainPanelMinimumWidth(PanelWidth.PX_160);
        copy.setMainPanelMaximumWidth(PanelWidth.PX_240);
        copy.setDetailsPanelMinimumWidth(PanelWidth.PX_120);
        copy.setDetailsPanelMaximumWidth(PanelWidth.PX_200);
        copy.setMainPanelPlacement(HudCorner.BOTTOM_RIGHT, new Offset(12, -18));
        copy.setDetailsPanelPlacement(HudCorner.BOTTOM_LEFT, new Offset(-9, 21));

        assertTrue(original.enabled());
        assertFalse(original.accessibilitySettingsEnabled());
        assertSame(HudScale.NORMAL, original.hudScale());
        assertSame(HudScale.COMPACT, original.detailsHudScale());
        assertSame(CoordinateDisplayMode.DECIMAL_ONLY, original.coordinateDisplay());
        assertFalse(original.coordinateLensEnabled());
        assertSame(CoordinateCopyFormat.PLAIN, original.coordinateCopyFormat());
        assertFalse(original.biomeThemeOverrideEnabled());
        assertFalse(original.biomeTransitionEnabled());
        assertFalse(original.movementSpeedEnabled());
        assertFalse(original.targetLingerEnabled());
        assertSame(TargetNameMode.API_ACCURATE, original.targetNameMode());
        assertSame(ViewDirectionDisplay.ON, original.viewDirectionDisplay());
        assertSame(HudCorner.TOP_LEFT, original.corner());
        assertEquals(Offset.ZERO, original.mainPanelOffset());
        assertEquals(PanelWidthLimits.AUTOMATIC, original.mainPanelWidthLimits());
        assertSame(HudCorner.TOP_RIGHT, original.detailsCorner());
        assertEquals(Offset.ZERO, original.detailsPanelOffset());
        assertEquals(PanelWidthLimits.AUTOMATIC, original.detailsPanelWidthLimits());
        assertFalse(copy.enabled());
        assertTrue(copy.accessibilitySettingsEnabled());
        assertSame(HudScale.HUGE, copy.hudScale());
        assertSame(HudScale.EXTRA_LARGE, copy.detailsHudScale());
        assertSame(CoordinateDisplayMode.BOTH, copy.coordinateDisplay());
        assertTrue(copy.coordinateLensEnabled());
        assertSame(CoordinateCopyFormat.CMI_TPPOS, copy.coordinateCopyFormat());
        assertTrue(copy.biomeThemeOverrideEnabled());
        assertTrue(copy.biomeTransitionEnabled());
        assertTrue(copy.movementSpeedEnabled());
        assertTrue(copy.targetLingerEnabled());
        assertSame(TargetNameMode.FRIENDLY, copy.targetNameMode());
        assertSame(ViewDirectionDisplay.WITH_DETAILS, copy.viewDirectionDisplay());
        assertSame(HudCorner.BOTTOM_RIGHT, copy.corner());
        assertEquals(new Offset(12, -18), copy.mainPanelOffset());
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_160, PanelWidth.PX_240),
            copy.mainPanelWidthLimits()
        );
        assertSame(HudCorner.BOTTOM_LEFT, copy.detailsCorner());
        assertEquals(new Offset(-9, 21), copy.detailsPanelOffset());
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_120, PanelWidth.PX_200),
            copy.detailsPanelWidthLimits()
        );
    }

    @Test
    void disablingAccessibilityReturnsOnlyOversizedPanelsToNormal() {
        LocatorHudSettings settings = LocatorHudSettings.defaults();
        settings.setAccessibilitySettingsEnabled(true);
        settings.setHudScale(HudScale.HUGE);
        settings.setDetailsHudScale(HudScale.SMALL);

        settings.setAccessibilitySettingsEnabled(false);

        assertFalse(settings.accessibilitySettingsEnabled());
        assertSame(HudScale.NORMAL, settings.hudScale());
        assertSame(HudScale.SMALL, settings.detailsHudScale());
    }

    @Test
    void replacementValidatesNullableValuesAndCopiesLegacyMirrors() {
        LocatorHudSettings source = LocatorHudSettings.defaults();
        source.setCorner(null);
        source.setPalette(null);
        source.setWorldNameEnabled(false);

        LocatorHudSettings replacement = LocatorHudSettings.defaults();
        replacement.replaceWith(source);

        assertSame(HudCorner.TOP_LEFT, replacement.corner());
        assertSame(ColorPalette.OCEAN, replacement.palette());
        assertFalse(replacement.worldNameEnabled());
    }

    @Test
    void clampsPanelOffsetsDuringValidation() {
        LocatorHudSettings settings = LocatorHudSettings.defaults();

        settings.setMainPanelPlacement(HudCorner.TOP_LEFT, new Offset(500, -500));
        settings.setDetailsPanelPlacement(HudCorner.BOTTOM_RIGHT, new Offset(-500, 500));

        assertEquals(new Offset(64, -64), settings.mainPanelOffset());
        assertEquals(new Offset(-64, 64), settings.detailsPanelOffset());
    }

    @Test
    void keepsPanelWidthBoundsValidWhenEitherControlCrossesTheOther() {
        LocatorHudSettings settings = LocatorHudSettings.defaults();

        settings.setMainPanelMaximumWidth(PanelWidth.PX_160);
        settings.setMainPanelMinimumWidth(PanelWidth.PX_240);
        settings.setDetailsPanelMinimumWidth(PanelWidth.PX_200);
        settings.setDetailsPanelMaximumWidth(PanelWidth.PX_120);

        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_240, PanelWidth.PX_240),
            settings.mainPanelWidthLimits()
        );
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_120, PanelWidth.PX_120),
            settings.detailsPanelWidthLimits()
        );
    }

}
