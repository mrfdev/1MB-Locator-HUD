package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class HudScaleTest {
    @Test
    void scalesTheWholePanelWithoutUnderestimatingItsScreenBounds() {
        assertEquals(80, HudScale.COMPACT.scaleDimension(100));
        assertEquals(90, HudScale.SMALL.scaleDimension(100));
        assertEquals(100, HudScale.NORMAL.scaleDimension(100));
        assertEquals(81, HudScale.COMPACT.scaleDimension(101));
    }
}
