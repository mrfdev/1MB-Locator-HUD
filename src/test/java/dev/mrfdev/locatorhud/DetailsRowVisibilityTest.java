package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class DetailsRowVisibilityTest {
    @Test
    void keepsEnabledEmptyTargetsVisibleWhenAutoHideIsOff() {
        DetailsRowVisibility rows = DetailsRowVisibility.resolve(
            true,
            true,
            TargetValue.EMPTY,
            true,
            TargetValue.EMPTY,
            true,
            TargetValue.EMPTY,
            false
        );

        assertTrue(rows.biome());
        assertTrue(rows.targetBlock());
        assertTrue(rows.targetFluid());
        assertTrue(rows.targetEntity());
        assertEquals(4, rows.rowCount());
    }

    @Test
    void hidesOnlyEmptyTargetsWhenAutoHideIsOn() {
        DetailsRowVisibility rows = DetailsRowVisibility.resolve(
            true,
            true,
            TargetValue.EMPTY,
            true,
            "water",
            true,
            "  ",
            true
        );

        assertTrue(rows.biome());
        assertFalse(rows.targetBlock());
        assertTrue(rows.targetFluid());
        assertFalse(rows.targetEntity());
        assertEquals(2, rows.rowCount());
    }

    @Test
    void leavesOnlyTheBiomeForTheScreenshotExample() {
        DetailsRowVisibility rows = DetailsRowVisibility.resolve(
            true,
            true,
            TargetValue.EMPTY,
            true,
            TargetValue.EMPTY,
            true,
            TargetValue.EMPTY,
            true
        );

        assertTrue(rows.biome());
        assertFalse(rows.targetBlock());
        assertFalse(rows.targetFluid());
        assertFalse(rows.targetEntity());
        assertEquals(1, rows.rowCount());
    }

    @Test
    void suppressesThePanelWhenEveryEnabledTargetIsEmptyAndBiomeIsOff() {
        DetailsRowVisibility rows = DetailsRowVisibility.resolve(
            false,
            true,
            TargetValue.EMPTY,
            true,
            null,
            true,
            "",
            true
        );

        assertTrue(rows.isEmpty());
        assertEquals(0, rows.rowCount());
    }

    @Test
    void disabledTargetsStayHiddenEvenWhenTheyHaveValues() {
        DetailsRowVisibility rows = DetailsRowVisibility.resolve(
            false,
            false,
            "stone",
            false,
            "water",
            false,
            "zombie",
            false
        );

        assertTrue(rows.isEmpty());
    }
}
