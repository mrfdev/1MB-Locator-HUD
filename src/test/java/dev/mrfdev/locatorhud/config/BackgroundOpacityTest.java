package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class BackgroundOpacityTest {
    @Test
    void exposesTheExpectedOpacityStopsInAscendingOrder() {
        assertEquals(7, BackgroundOpacity.values().length);
        assertEquals("OFF (minimal)", BackgroundOpacity.OFF.displayName());
        assertEquals(0, BackgroundOpacity.OFF.percentage());
        assertEquals(7, BackgroundOpacity.FAINT.percentage());
        assertEquals(24, BackgroundOpacity.LIGHT.percentage());
        assertEquals(55, BackgroundOpacity.SOFT.percentage());
        assertEquals(72, BackgroundOpacity.BALANCED.percentage());
        assertEquals(88, BackgroundOpacity.STRONG.percentage());
        assertEquals(100, BackgroundOpacity.SOLID.percentage());
    }

    @Test
    void convertsPercentagesToRoundedAlphaBytes() {
        assertEquals(0, BackgroundOpacity.OFF.alpha());
        assertEquals(18, BackgroundOpacity.FAINT.alpha());
        assertEquals(61, BackgroundOpacity.LIGHT.alpha());
        assertEquals(140, BackgroundOpacity.SOFT.alpha());
        assertEquals(184, BackgroundOpacity.BALANCED.alpha());
        assertEquals(224, BackgroundOpacity.STRONG.alpha());
        assertEquals(255, BackgroundOpacity.SOLID.alpha());
        assertEquals(0x8C123456, BackgroundOpacity.SOFT.applyTo(0xFF123456));
    }

    @Test
    void snapsSliderPositionsToTheNearestStop() {
        for (BackgroundOpacity opacity : BackgroundOpacity.values()) {
            assertSame(opacity, BackgroundOpacity.nearestSliderPosition(opacity.sliderPosition()));
        }
        assertSame(BackgroundOpacity.OFF, BackgroundOpacity.nearestSliderPosition(-1.0));
        assertSame(BackgroundOpacity.SOLID, BackgroundOpacity.nearestSliderPosition(2.0));
        assertSame(BackgroundOpacity.LIGHT, BackgroundOpacity.nearestSliderPosition(0.20));
    }

    @Test
    void onlyOffUsesTheMinimalPanelLayout() {
        assertFalse(BackgroundOpacity.OFF.drawsPanel());
        for (BackgroundOpacity opacity : BackgroundOpacity.values()) {
            if (opacity != BackgroundOpacity.OFF) {
                assertTrue(opacity.drawsPanel());
            }
        }
    }

    @Test
    void retainsTheExistingSerializedEnumNames() {
        assertSame(BackgroundOpacity.SOFT, BackgroundOpacity.valueOf("SOFT"));
        assertSame(BackgroundOpacity.BALANCED, BackgroundOpacity.valueOf("BALANCED"));
        assertSame(BackgroundOpacity.STRONG, BackgroundOpacity.valueOf("STRONG"));
    }
}
