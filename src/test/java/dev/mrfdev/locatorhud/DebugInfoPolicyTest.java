package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class DebugInfoPolicyTest {
    @Test
    void preservesConfiguredDetailsWhenTheServerAllowsFullDebugInformation() {
        assertTrue(DebugInfoPolicy.allowsCoordinates(false));
        assertSame(
            CoordinateDisplayMode.BOTH,
            DebugInfoPolicy.coordinateDisplay(false, CoordinateDisplayMode.BOTH)
        );
        assertTrue(DebugInfoPolicy.coordinateLensEnabled(false, true));
        assertTrue(DebugInfoPolicy.allowsTargetDetails(false));
        assertTrue(DebugInfoPolicy.targetDetailEnabled(false, true));
        assertFalse(DebugInfoPolicy.coordinateLensEnabled(false, false));
        assertFalse(DebugInfoPolicy.targetDetailEnabled(false, false));
    }

    @Test
    void suppressesCoordinatesLensCopyAndTargetsUnderServerReducedDebug() {
        assertFalse(DebugInfoPolicy.allowsCoordinates(true));
        assertSame(
            CoordinateDisplayMode.HIDDEN,
            DebugInfoPolicy.coordinateDisplay(true, CoordinateDisplayMode.BOTH)
        );
        assertFalse(DebugInfoPolicy.coordinateLensEnabled(true, true));
        assertFalse(DebugInfoPolicy.allowsTargetDetails(true));
        assertFalse(DebugInfoPolicy.targetDetailEnabled(true, true));
    }
}
