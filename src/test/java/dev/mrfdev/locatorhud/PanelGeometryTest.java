package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.mrfdev.locatorhud.PanelGeometry.Offset;
import dev.mrfdev.locatorhud.PanelGeometry.PanelSize;
import dev.mrfdev.locatorhud.PanelGeometry.Placement;
import dev.mrfdev.locatorhud.PanelGeometry.Screen;
import dev.mrfdev.locatorhud.config.HudCorner;
import org.junit.jupiter.api.Test;

final class PanelGeometryTest {
    private static final Screen SCREEN = new Screen(320, 180);
    private static final HudLayout PANEL_LAYOUT = HudLayout.forPanel(true);

    @Test
    void measuresCurrentAndFutureAccessibilityScalesWithoutUnderestimatingBounds() {
        assertEquals(
            new PanelSize(119, 33, 72, 20),
            PanelGeometry.measure(PANEL_LAYOUT, 100, 9, 2, 60)
        );
        assertEquals(
            new PanelSize(119, 33, 119, 33),
            PanelGeometry.measure(PANEL_LAYOUT, 100, 9, 2, 100)
        );
        assertEquals(
            new PanelSize(119, 33, 179, 50),
            PanelGeometry.measure(PANEL_LAYOUT, 100, 9, 2, 150)
        );
    }

    @Test
    void preservesCurrentWidthLimitsAndConstrainsFutureEnlargedPanels() {
        assertEquals(285, PanelGeometry.maximumContentWidth(SCREEN, PANEL_LAYOUT, 60));
        assertEquals(285, PanelGeometry.maximumContentWidth(SCREEN, PANEL_LAYOUT, 100));
        assertEquals(183, PanelGeometry.maximumContentWidth(SCREEN, PANEL_LAYOUT, 150));

        HudLayout minimal = HudLayout.forPanel(false);
        assertEquals(310, PanelGeometry.maximumContentWidth(SCREEN, minimal, 100));
        assertEquals(206, PanelGeometry.maximumContentWidth(SCREEN, minimal, 150));
    }

    @Test
    void anchorsEveryCornerInsideItsMargin() {
        PanelSize size = PanelGeometry.measure(PANEL_LAYOUT, 100, 9, 2, 100);

        assertEquals(new Placement(8, 8, 119, 33), place(HudCorner.TOP_LEFT, size));
        assertEquals(new Placement(193, 8, 119, 33), place(HudCorner.TOP_RIGHT, size));
        assertEquals(new Placement(8, 139, 119, 33), place(HudCorner.BOTTOM_LEFT, size));
        assertEquals(new Placement(193, 139, 119, 33), place(HudCorner.BOTTOM_RIGHT, size));
    }

    @Test
    void clampsOffsetsToTheUsableScreenBounds() {
        PanelSize size = PanelGeometry.measure(PANEL_LAYOUT, 100, 9, 2, 100);

        assertEquals(
            new Placement(8, 8, 119, 33),
            PanelGeometry.place(SCREEN, HudCorner.TOP_LEFT, PANEL_LAYOUT, size, new Offset(-500, -500))
        );
        assertEquals(
            new Placement(193, 139, 119, 33),
            PanelGeometry.place(SCREEN, HudCorner.TOP_LEFT, PANEL_LAYOUT, size, new Offset(500, 500))
        );
        assertEquals(
            new Placement(173, 8, 119, 33),
            PanelGeometry.place(SCREEN, HudCorner.TOP_RIGHT, PANEL_LAYOUT, size, new Offset(-20, 0))
        );
    }

    @Test
    void handlesTightAndOversizedPanelsDeterministically() {
        Screen tightScreen = new Screen(100, 50);
        PanelSize tightSize = new PanelSize(95, 45, 95, 45);
        PanelSize oversized = new PanelSize(120, 60, 120, 60);

        assertEquals(
            new Placement(5, 5, 95, 45),
            PanelGeometry.place(tightScreen, HudCorner.TOP_LEFT, PANEL_LAYOUT, tightSize)
        );
        assertEquals(
            new Placement(0, 0, 95, 45),
            PanelGeometry.place(tightScreen, HudCorner.BOTTOM_RIGHT, PANEL_LAYOUT, tightSize)
        );
        assertEquals(
            new Placement(0, 0, 120, 60),
            PanelGeometry.place(tightScreen, HudCorner.TOP_LEFT, PANEL_LAYOUT, oversized)
        );
        assertEquals(
            new Placement(-20, -10, 120, 60),
            PanelGeometry.place(tightScreen, HudCorner.BOTTOM_RIGHT, PANEL_LAYOUT, oversized)
        );
    }

    @Test
    void stacksPanelsAwayFromTheirSharedCornerAndClampsAtTheOppositeEdge() {
        Placement topMain = new Placement(8, 8, 119, 33);
        Placement topDetails = new Placement(8, 8, 80, 20);
        Placement bottomMain = new Placement(8, 139, 119, 33);
        Placement bottomDetails = new Placement(8, 152, 80, 20);

        assertEquals(
            new Placement(8, 45, 80, 20),
            PanelGeometry.stack(SCREEN, topDetails, topMain, PANEL_LAYOUT, HudCorner.TOP_LEFT, 4)
        );
        assertEquals(
            new Placement(8, 115, 80, 20),
            PanelGeometry.stack(SCREEN, bottomDetails, bottomMain, PANEL_LAYOUT, HudCorner.BOTTOM_LEFT, 4)
        );

        Placement tallMain = new Placement(8, 8, 119, 160);
        assertEquals(
            new Placement(8, 152, 80, 20),
            PanelGeometry.stack(SCREEN, topDetails, tallMain, PANEL_LAYOUT, HudCorner.TOP_LEFT, 4)
        );
    }

    @Test
    void rejectsInvalidDimensionsScalesAndGaps() {
        assertThrows(IllegalArgumentException.class, () -> new Screen(-1, 100));
        assertThrows(
            IllegalArgumentException.class,
            () -> PanelGeometry.measure(PANEL_LAYOUT, -1, 9, 1, 100)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> PanelGeometry.measure(PANEL_LAYOUT, 1, 9, 1, 0)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> PanelGeometry.stack(
                SCREEN,
                new Placement(0, 0, 1, 1),
                new Placement(0, 0, 1, 1),
                PANEL_LAYOUT,
                HudCorner.TOP_LEFT,
                -1
            )
        );
    }

    private static Placement place(HudCorner corner, PanelSize size) {
        return PanelGeometry.place(SCREEN, corner, PANEL_LAYOUT, size);
    }
}
