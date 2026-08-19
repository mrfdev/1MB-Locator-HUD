package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

final class HudScaleTest {
    @Test
    void exposesStandardAndAccessibilityScaleChoicesInAscendingOrder() {
        assertEquals(
            List.of(60, 70, 80, 90, 100, 110, 125, 150),
            List.of(HudScale.values()).stream().map(HudScale::percentage).toList()
        );
        assertEquals(
            List.of(
                HudScale.EXTRA_SMALL,
                HudScale.VERY_SMALL,
                HudScale.COMPACT,
                HudScale.SMALL,
                HudScale.NORMAL
            ),
            HudScale.choices(false)
        );
        assertEquals(List.of(HudScale.values()), HudScale.choices(true));
        assertEquals("value.locatorhud.hud_scale.large", HudScale.LARGE.translationKey());
        assertEquals(
            "value.locatorhud.hud_scale.extra_large",
            HudScale.EXTRA_LARGE.translationKey()
        );
        assertEquals("value.locatorhud.hud_scale.huge", HudScale.HUGE.translationKey());
        assertSame(HudScale.COMPACT, HudScale.valueOf("COMPACT"));
        assertSame(HudScale.SMALL, HudScale.valueOf("SMALL"));
        assertSame(HudScale.NORMAL, HudScale.valueOf("NORMAL"));
    }

    @Test
    void identifiesAccessibilityOnlyChoicesAndProvidesASafeStandardFallback() {
        assertFalse(HudScale.NORMAL.accessibilityOnly());
        assertTrue(HudScale.LARGE.accessibilityOnly());
        assertTrue(HudScale.EXTRA_LARGE.accessibilityOnly());
        assertTrue(HudScale.HUGE.accessibilityOnly());
        assertSame(HudScale.SMALL, HudScale.SMALL.standardFallback());
        assertSame(HudScale.NORMAL, HudScale.HUGE.standardFallback());
    }
}
