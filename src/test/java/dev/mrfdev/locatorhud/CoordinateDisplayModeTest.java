package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
        for (WorldNameDisplay display : new WorldNameDisplay[] {
            WorldNameDisplay.IN_FRONT,
            WorldNameDisplay.BEHIND
        }) {
            assertTrue(CoordinateDisplayMode.DECIMAL_ONLY.worldSharesDecimalRow(display));
            assertTrue(CoordinateDisplayMode.BOTH.worldSharesDecimalRow(display));
            assertFalse(CoordinateDisplayMode.BLOCK_ONLY.worldSharesDecimalRow(display));
            assertTrue(CoordinateDisplayMode.BLOCK_ONLY.worldSharesBlockRow(display));
            assertFalse(CoordinateDisplayMode.BOTH.worldSharesBlockRow(display));
            assertTrue(CoordinateDisplayMode.HIDDEN.worldUsesOwnRow(display));
        }

        for (CoordinateDisplayMode mode : CoordinateDisplayMode.values()) {
            assertFalse(mode.worldSharesDecimalRow(WorldNameDisplay.OFF));
            assertFalse(mode.worldSharesBlockRow(WorldNameDisplay.OFF));
            assertFalse(mode.worldUsesOwnRow(WorldNameDisplay.OFF));
        }
    }

    @Test
    void countsRowsWithAndWithoutTheWorldOrViewRow() {
        for (WorldNameDisplay display : new WorldNameDisplay[] {
            WorldNameDisplay.IN_FRONT,
            WorldNameDisplay.BEHIND
        }) {
            assertEquals(2, CoordinateDisplayMode.DECIMAL_ONLY.coreRows(display, true));
            assertEquals(3, CoordinateDisplayMode.BOTH.coreRows(display, true));
            assertEquals(2, CoordinateDisplayMode.BLOCK_ONLY.coreRows(display, true));
            assertEquals(2, CoordinateDisplayMode.HIDDEN.coreRows(display, true));

            assertEquals(1, CoordinateDisplayMode.DECIMAL_ONLY.coreRows(display, false));
            assertEquals(2, CoordinateDisplayMode.BOTH.coreRows(display, false));
            assertEquals(1, CoordinateDisplayMode.BLOCK_ONLY.coreRows(display, false));
            assertEquals(1, CoordinateDisplayMode.HIDDEN.coreRows(display, false));
        }

        assertEquals(2, CoordinateDisplayMode.DECIMAL_ONLY.coreRows(WorldNameDisplay.OFF, true));
        assertEquals(3, CoordinateDisplayMode.BOTH.coreRows(WorldNameDisplay.OFF, true));
        assertEquals(2, CoordinateDisplayMode.BLOCK_ONLY.coreRows(WorldNameDisplay.OFF, true));
        assertEquals(1, CoordinateDisplayMode.HIDDEN.coreRows(WorldNameDisplay.OFF, true));

        assertEquals(1, CoordinateDisplayMode.DECIMAL_ONLY.coreRows(WorldNameDisplay.OFF, false));
        assertEquals(2, CoordinateDisplayMode.BOTH.coreRows(WorldNameDisplay.OFF, false));
        assertEquals(1, CoordinateDisplayMode.BLOCK_ONLY.coreRows(WorldNameDisplay.OFF, false));
        assertEquals(0, CoordinateDisplayMode.HIDDEN.coreRows(WorldNameDisplay.OFF, false));
    }

    @Test
    void composesCoordinateRowsInTheRequestedWorldOrder() {
        for (WorldNameDisplay display : WorldNameDisplay.values()) {
            List<CoordinateRowSegment> sharedRow = switch (display) {
                case IN_FRONT -> List.of(CoordinateRowSegment.WORLD, CoordinateRowSegment.COORDINATES);
                case BEHIND -> List.of(CoordinateRowSegment.COORDINATES, CoordinateRowSegment.WORLD);
                case OFF -> List.of(CoordinateRowSegment.COORDINATES);
            };

            assertEquals(sharedRow, CoordinateDisplayMode.DECIMAL_ONLY.decimalRowSegments(display));
            assertEquals(List.of(), CoordinateDisplayMode.DECIMAL_ONLY.blockRowSegments(display));

            assertEquals(List.of(), CoordinateDisplayMode.BLOCK_ONLY.decimalRowSegments(display));
            assertEquals(sharedRow, CoordinateDisplayMode.BLOCK_ONLY.blockRowSegments(display));

            assertEquals(sharedRow, CoordinateDisplayMode.BOTH.decimalRowSegments(display));
            assertEquals(
                List.of(CoordinateRowSegment.COORDINATES),
                CoordinateDisplayMode.BOTH.blockRowSegments(display)
            );

            assertEquals(List.of(), CoordinateDisplayMode.HIDDEN.decimalRowSegments(display));
            assertEquals(List.of(), CoordinateDisplayMode.HIDDEN.blockRowSegments(display));
        }
    }
}
