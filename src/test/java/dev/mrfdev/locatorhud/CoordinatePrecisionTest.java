package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CoordinatePrecisionTest {
    @Test
    void blockPrecisionUsesContainingBlockForNegativeCoordinates() {
        assertEquals("-2", CoordinatePrecision.BLOCK.format(-1.1D));
        assertEquals("1", CoordinatePrecision.BLOCK.format(1.9D));
    }

    @Test
    void noDecimalPrecisionRoundsWithoutBecomingBlockCoordinates() {
        assertEquals("None", CoordinatePrecision.NONE.displayName());
        assertEquals("12", CoordinatePrecision.NONE.format(12.49D));
        assertEquals("13", CoordinatePrecision.NONE.format(12.5D));
        assertEquals("-1", CoordinatePrecision.NONE.format(-1.1D));
        assertEquals("-2", CoordinatePrecision.NONE.format(-1.5D));
        assertEquals("0", CoordinatePrecision.NONE.format(-0.4D));
    }

    @Test
    void decimalPrecisionsUseStableDotFormatting() {
        assertEquals("12.3", CoordinatePrecision.ONE_DECIMAL.format(12.34D));
        assertEquals("12.35", CoordinatePrecision.TWO_DECIMALS.format(12.345D));
    }

    @Test
    void nonFiniteCoordinatesRemainUnavailableAtEveryPrecision() {
        for (CoordinatePrecision precision : CoordinatePrecision.values()) {
            assertEquals("?", precision.format(Double.NaN));
            assertEquals("?", precision.format(Double.POSITIVE_INFINITY));
            assertEquals("?", precision.format(Double.NEGATIVE_INFINITY));
        }
    }
}
