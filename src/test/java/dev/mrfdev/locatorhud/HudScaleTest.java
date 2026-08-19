package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

final class HudScaleTest {
    @Test
    void scalesTheWholePanelWithoutUnderestimatingItsScreenBounds() {
        assertEquals(60, HudScale.EXTRA_SMALL.scaleDimension(100));
        assertEquals(70, HudScale.VERY_SMALL.scaleDimension(100));
        assertEquals(80, HudScale.COMPACT.scaleDimension(100));
        assertEquals(90, HudScale.SMALL.scaleDimension(100));
        assertEquals(100, HudScale.NORMAL.scaleDimension(100));
        assertEquals(61, HudScale.EXTRA_SMALL.scaleDimension(101));
        assertEquals(71, HudScale.VERY_SMALL.scaleDimension(101));
        assertEquals(81, HudScale.COMPACT.scaleDimension(101));
    }

    @Test
    void exposesAllFiveScaleChoicesInAscendingOrder() {
        assertEquals(5, HudScale.values().length);
        assertEquals("Extra small (60%)", HudScale.values()[0].displayName());
        assertEquals(60, HudScale.values()[0].percentage());
        assertEquals("Very small (70%)", HudScale.values()[1].displayName());
        assertEquals(70, HudScale.values()[1].percentage());
        assertEquals("Compact (80%)", HudScale.values()[2].displayName());
        assertEquals(80, HudScale.values()[2].percentage());
        assertEquals("Small (90%)", HudScale.values()[3].displayName());
        assertEquals(90, HudScale.values()[3].percentage());
        assertEquals("Normal (100%)", HudScale.values()[4].displayName());
        assertEquals(100, HudScale.values()[4].percentage());
        assertSame(HudScale.COMPACT, HudScale.valueOf("COMPACT"));
        assertSame(HudScale.SMALL, HudScale.valueOf("SMALL"));
        assertSame(HudScale.NORMAL, HudScale.valueOf("NORMAL"));
    }

    @Test
    void mapsAllFiveChoicesAcrossTheFullSliderTrack() {
        assertEquals(0.0D, HudScale.EXTRA_SMALL.sliderPosition());
        assertEquals(0.25D, HudScale.VERY_SMALL.sliderPosition());
        assertEquals(0.5D, HudScale.COMPACT.sliderPosition());
        assertEquals(0.75D, HudScale.SMALL.sliderPosition());
        assertEquals(1.0D, HudScale.NORMAL.sliderPosition());

        for (HudScale scale : HudScale.values()) {
            assertSame(scale, HudScale.nearestSliderPosition(scale.sliderPosition()));
        }
        assertSame(HudScale.EXTRA_SMALL, HudScale.nearestSliderPosition(-1.0D));
        assertSame(HudScale.NORMAL, HudScale.nearestSliderPosition(2.0D));
        assertSame(HudScale.COMPACT, HudScale.nearestSliderPosition(0.4D));
        assertSame(HudScale.EXTRA_SMALL, HudScale.nearestSliderPosition(0.125D));
    }
}
