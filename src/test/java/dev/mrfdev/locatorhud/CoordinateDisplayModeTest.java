package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CoordinateDisplayModeTest {
    @Test
    void exposesTheFourRequestedCoordinateLayouts() {
        assertTrue(CoordinateDisplayMode.DECIMAL_ONLY.showsDecimal());
        assertFalse(CoordinateDisplayMode.DECIMAL_ONLY.showsBlock());
        assertTrue(CoordinateDisplayMode.BLOCK_ONLY.showsBlock());
        assertFalse(CoordinateDisplayMode.BLOCK_ONLY.showsDecimal());
        assertEquals(2, CoordinateDisplayMode.BOTH.coordinateRows());
        assertEquals(0, CoordinateDisplayMode.HIDDEN.coordinateRows());
    }

    @Test
    void placesWorldOnTheFirstAvailableCoordinateRow() {
        assertTrue(CoordinateDisplayMode.DECIMAL_ONLY.worldSharesDecimalRow(true));
        assertTrue(CoordinateDisplayMode.BOTH.worldSharesDecimalRow(true));
        assertFalse(CoordinateDisplayMode.BLOCK_ONLY.worldSharesDecimalRow(true));
        assertTrue(CoordinateDisplayMode.BLOCK_ONLY.worldSharesBlockRow(true));
        assertFalse(CoordinateDisplayMode.BOTH.worldSharesBlockRow(true));
        assertTrue(CoordinateDisplayMode.HIDDEN.worldUsesOwnRow(true));

        for (CoordinateDisplayMode mode : CoordinateDisplayMode.values()) {
            assertFalse(mode.worldSharesDecimalRow(false));
            assertFalse(mode.worldSharesBlockRow(false));
            assertFalse(mode.worldUsesOwnRow(false));
        }
    }

    @Test
    void countsRowsWithAndWithoutTheWorld() {
        assertEquals(2, CoordinateDisplayMode.DECIMAL_ONLY.coreRows(true));
        assertEquals(3, CoordinateDisplayMode.BOTH.coreRows(true));
        assertEquals(2, CoordinateDisplayMode.BLOCK_ONLY.coreRows(true));
        assertEquals(2, CoordinateDisplayMode.HIDDEN.coreRows(true));

        assertEquals(2, CoordinateDisplayMode.DECIMAL_ONLY.coreRows(false));
        assertEquals(3, CoordinateDisplayMode.BOTH.coreRows(false));
        assertEquals(2, CoordinateDisplayMode.BLOCK_ONLY.coreRows(false));
        assertEquals(1, CoordinateDisplayMode.HIDDEN.coreRows(false));
    }
}
