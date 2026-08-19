package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class ViewAngleFormatterTest {
    @Test
    void roundsAndWrapsHorizontalRotation() {
        assertEquals("83°", ViewAngleFormatter.horizontal(83.4F));
        assertEquals("10°", ViewAngleFormatter.horizontal(370.0F));
        assertEquals("170°", ViewAngleFormatter.horizontal(-190.0F));
    }

    @Test
    void explainsVerticalRotationDirection() {
        assertEquals("37° down", ViewAngleFormatter.vertical(37.0F));
        assertEquals("15° up", ViewAngleFormatter.vertical(-15.0F));
        assertEquals("0° level", ViewAngleFormatter.vertical(0.0F));
    }

    @Test
    void formatsOneOrTwoDecimalsForBothAngles() {
        assertEquals("83.5°", ViewAngleFormatter.horizontal(83.456F, ViewAnglePrecision.ONE_DECIMAL));
        assertEquals("83.46°", ViewAngleFormatter.horizontal(83.456F, ViewAnglePrecision.TWO_DECIMALS));
        assertEquals("37.5° down", ViewAngleFormatter.vertical(37.456F, ViewAnglePrecision.ONE_DECIMAL));
        assertEquals("15.46° up", ViewAngleFormatter.vertical(-15.456F, ViewAnglePrecision.TWO_DECIMALS));
    }

    @Test
    void avoidsNegativeZeroAndUsesTheSelectedLevelPrecision() {
        assertEquals("0.0°", ViewAngleFormatter.horizontal(-0.04F, ViewAnglePrecision.ONE_DECIMAL));
        assertEquals("0.00° level", ViewAngleFormatter.vertical(-0.004F, ViewAnglePrecision.TWO_DECIMALS));
    }
}
