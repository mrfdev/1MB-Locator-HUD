package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

final class PanelWidthLimitsTest {
    private static final HudLayout PANEL_LAYOUT = HudLayout.forPanel(true);

    @Test
    void exposesAutomaticAndBoundedFortyPixelStops() {
        assertEquals(
            List.of(0, 120, 160, 200, 240, 280, 320),
            Arrays.stream(PanelWidth.values()).map(PanelWidth::pixels).toList()
        );
        assertEquals("value.locatorhud.panel_width.auto", PanelWidth.AUTO.translationKey());
        assertEquals("value.locatorhud.panel_width.pixels", PanelWidth.PX_200.translationKey());
    }

    @Test
    void automaticLimitsFollowContentUntilTheScreenCeiling() {
        assertEquals(150, PanelWidthLimits.AUTOMATIC.constrainContentWidth(150, 200, PANEL_LAYOUT));
        assertEquals(200, PanelWidthLimits.AUTOMATIC.constrainContentWidth(250, 200, PANEL_LAYOUT));
    }

    @Test
    void fixedLimitsApplyToTheWholeUnscaledPanelWidth() {
        PanelWidthLimits limits = new PanelWidthLimits(PanelWidth.PX_160, PanelWidth.PX_240);

        assertEquals(141, limits.constrainContentWidth(80, 400, PANEL_LAYOUT));
        assertEquals(180, limits.constrainContentWidth(180, 400, PANEL_LAYOUT));
        assertEquals(221, limits.constrainContentWidth(300, 400, PANEL_LAYOUT));
    }

    @Test
    void screenCeilingAlwaysWinsOverAConfiguredMinimum() {
        PanelWidthLimits limits = new PanelWidthLimits(PanelWidth.PX_320, PanelWidth.AUTO);

        assertEquals(90, limits.constrainContentWidth(50, 90, PANEL_LAYOUT));
    }

    @Test
    void fixedWidthRefersToTheWholePanelInEitherBackgroundMode() {
        PanelWidthLimits limits = new PanelWidthLimits(PanelWidth.PX_160, PanelWidth.PX_160);
        HudLayout minimalLayout = HudLayout.forPanel(false);

        int panelContent = limits.constrainContentWidth(20, 400, PANEL_LAYOUT);
        int minimalContent = limits.constrainContentWidth(20, 400, minimalLayout);

        assertEquals(160, PANEL_LAYOUT.panelWidth(panelContent));
        assertEquals(160, minimalLayout.panelWidth(minimalContent));
    }

    @Test
    void configuredMinimumStillFitsAtFutureAccessibilityScale() {
        PanelGeometry.Screen screen = new PanelGeometry.Screen(100, 80);
        int screenMaximum = PanelGeometry.maximumContentWidth(screen, PANEL_LAYOUT, 150);
        int contentWidth = new PanelWidthLimits(
            PanelWidth.PX_320,
            PanelWidth.AUTO
        ).constrainContentWidth(20, screenMaximum, PANEL_LAYOUT);
        PanelGeometry.PanelSize size = PanelGeometry.measure(
            PANEL_LAYOUT,
            contentWidth,
            9,
            1,
            150
        );

        assertEquals(37, contentWidth);
        assertEquals(84, size.scaledWidth());
    }

    @Test
    void changingEitherBoundRepairsACrossingPairInTheDirectionOfTheChange() {
        PanelWidthLimits initial = new PanelWidthLimits(PanelWidth.PX_160, PanelWidth.PX_240);

        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_240, PanelWidth.PX_240),
            initial.withMinimum(PanelWidth.PX_240)
        );
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_120, PanelWidth.PX_120),
            initial.withMaximum(PanelWidth.PX_120)
        );
    }

    @Test
    void normalizationUsesAutomaticFallbacksAndKeepsTheSafetyCap() {
        assertEquals(PanelWidthLimits.AUTOMATIC, PanelWidthLimits.normalized(null, null));
        assertEquals(
            new PanelWidthLimits(PanelWidth.PX_160, PanelWidth.PX_160),
            PanelWidthLimits.normalized(PanelWidth.PX_240, PanelWidth.PX_160)
        );
    }

    @Test
    void rejectsInvalidDimensionsAndDirectlyCrossedBounds() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new PanelWidthLimits(PanelWidth.PX_200, PanelWidth.PX_160)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> PanelWidthLimits.AUTOMATIC.constrainContentWidth(-1, 10, PANEL_LAYOUT)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> PanelWidthLimits.AUTOMATIC.constrainContentWidth(10, -1, PANEL_LAYOUT)
        );
    }
}
