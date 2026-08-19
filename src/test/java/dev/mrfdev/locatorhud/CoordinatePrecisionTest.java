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
    void decimalPrecisionsUseStableDotFormatting() {
        assertEquals("12.3", CoordinatePrecision.ONE_DECIMAL.format(12.34D));
        assertEquals("12.35", CoordinatePrecision.TWO_DECIMALS.format(12.345D));
    }
}
