package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.mrfdev.locatorhud.PanelGeometry.Offset;
import dev.mrfdev.locatorhud.PanelGeometry.PanelSize;
import dev.mrfdev.locatorhud.PanelGeometry.Placement;
import dev.mrfdev.locatorhud.PanelGeometry.Screen;
import dev.mrfdev.locatorhud.PanelPlacementPolicy.Result;
import dev.mrfdev.locatorhud.config.HudCorner;
import org.junit.jupiter.api.Test;

final class PanelPlacementPolicyTest {
    private static final Screen SCREEN = new Screen(320, 180);
    private static final HudLayout LAYOUT = HudLayout.forPanel(true);
    private static final PanelSize SIZE = new PanelSize(100, 30, 100, 30);

    @Test
    void choosesTheNearestCornerFromTheDraggedPanelCenter() {
        assertEquals(HudCorner.TOP_LEFT, resolve(20, 20).corner());
        assertEquals(HudCorner.TOP_RIGHT, resolve(200, 20).corner());
        assertEquals(HudCorner.BOTTOM_LEFT, resolve(20, 120).corner());
        assertEquals(HudCorner.BOTTOM_RIGHT, resolve(200, 120).corner());
    }

    @Test
    void snapsSmallOffsetsBackToTheCornerAnchor() {
        Result result = resolve(13, 3);

        assertEquals(HudCorner.TOP_LEFT, result.corner());
        assertEquals(Offset.ZERO, result.offset());
        assertEquals(new Placement(8, 8, 100, 30), result.placement());
    }

    @Test
    void clampsOffsetsAndNormalizesScreenEdgeClamping() {
        Result inward = resolve(100, 70);
        assertEquals(HudCorner.TOP_LEFT, inward.corner());
        assertEquals(new Offset(64, 62), inward.offset());
        assertEquals(new Placement(72, 70, 100, 30), inward.placement());

        Result beyondEdge = resolve(-200, -200);
        assertEquals(HudCorner.TOP_LEFT, beyondEdge.corner());
        assertEquals(Offset.ZERO, beyondEdge.offset());
        assertEquals(new Placement(8, 8, 100, 30), beyondEdge.placement());
    }

    @Test
    void clampsPersistedOffsetsIndependently() {
        assertEquals(
            new Offset(-64, 64),
            PanelPlacementPolicy.clampOffset(new Offset(-500, 500))
        );
    }

    private static Result resolve(int x, int y) {
        return PanelPlacementPolicy.resolve(SCREEN, LAYOUT, SIZE, x, y);
    }
}
