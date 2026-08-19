package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class HudLayoutTest {
    @Test
    void minimalLayoutRemovesPanelChromeAndTightensTheTextBounds() {
        HudLayout panel = HudLayout.forPanel(true);
        HudLayout minimal = HudLayout.forPanel(false);

        assertTrue(panel.drawsPanel());
        assertFalse(minimal.drawsPanel());
        assertEquals(119, panel.panelWidth(100));
        assertEquals(102, minimal.panelWidth(100));
        assertEquals(33, panel.panelHeight(9, 2));
        assertEquals(20, minimal.panelHeight(9, 2));
        assertEquals(9, panel.segmentGap());
        assertEquals(5, minimal.segmentGap());
        assertEquals(" • ", panel.detailDivider());
        assertEquals(" • ", minimal.detailDivider());
        assertEquals(" / ", panel.coordinateDivider());
        assertEquals("/", minimal.coordinateDivider());
    }
}
