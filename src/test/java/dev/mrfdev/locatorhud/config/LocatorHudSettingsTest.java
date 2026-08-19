package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import org.junit.jupiter.api.Test;

final class LocatorHudSettingsTest {
    @Test
    void copiesSettingsWithoutSharingSubsequentMutations() {
        LocatorHudSettings original = LocatorHudSettings.defaults();
        LocatorHudSettings copy = original.copy();

        copy.setEnabled(false);
        copy.setCoordinateDisplay(CoordinateDisplayMode.BOTH);
        copy.setCoordinateLensEnabled(true);
        copy.setCoordinateCopyFormat(CoordinateCopyFormat.CMI_TPPOS);
        copy.setBiomeThemeOverrideEnabled(true);
        copy.setBiomeTransitionEnabled(true);
        copy.setMovementSpeedEnabled(true);
        copy.setTargetLingerEnabled(true);
        copy.setTargetNameMode(TargetNameMode.FRIENDLY);
        copy.setViewDirectionDisplay(ViewDirectionDisplay.WITH_DETAILS);

        assertTrue(original.enabled());
        assertSame(CoordinateDisplayMode.DECIMAL_ONLY, original.coordinateDisplay());
        assertFalse(original.coordinateLensEnabled());
        assertSame(CoordinateCopyFormat.PLAIN, original.coordinateCopyFormat());
        assertFalse(original.biomeThemeOverrideEnabled());
        assertFalse(original.biomeTransitionEnabled());
        assertFalse(original.movementSpeedEnabled());
        assertFalse(original.targetLingerEnabled());
        assertSame(TargetNameMode.API_ACCURATE, original.targetNameMode());
        assertSame(ViewDirectionDisplay.ON, original.viewDirectionDisplay());
        assertFalse(copy.enabled());
        assertSame(CoordinateDisplayMode.BOTH, copy.coordinateDisplay());
        assertTrue(copy.coordinateLensEnabled());
        assertSame(CoordinateCopyFormat.CMI_TPPOS, copy.coordinateCopyFormat());
        assertTrue(copy.biomeThemeOverrideEnabled());
        assertTrue(copy.biomeTransitionEnabled());
        assertTrue(copy.movementSpeedEnabled());
        assertTrue(copy.targetLingerEnabled());
        assertSame(TargetNameMode.FRIENDLY, copy.targetNameMode());
        assertSame(ViewDirectionDisplay.WITH_DETAILS, copy.viewDirectionDisplay());
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

}
