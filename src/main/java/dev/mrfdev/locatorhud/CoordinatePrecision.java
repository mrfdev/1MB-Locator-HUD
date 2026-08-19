package dev.mrfdev.locatorhud;

import java.util.Locale;

public enum CoordinatePrecision {
    BLOCK("value.locatorhud.coordinate_precision.block", 0),
    NONE("value.locatorhud.coordinate_precision.none", 0),
    ONE_DECIMAL("value.locatorhud.coordinate_precision.one_decimal", 1),
    TWO_DECIMALS("value.locatorhud.coordinate_precision.two_decimals", 2);

    private final String translationKey;
    private final int decimalPlaces;

    CoordinatePrecision(String translationKey, int decimalPlaces) {
        this.translationKey = translationKey;
        this.decimalPlaces = decimalPlaces;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public String format(double coordinate) {
        if (!Double.isFinite(coordinate)) {
            return "?";
        }
        if (this == BLOCK) {
            return Long.toString((long) Math.floor(coordinate));
        }
        String formatted = String.format(Locale.ROOT, "%." + this.decimalPlaces + "f", coordinate);
        return this == NONE && formatted.equals("-0") ? "0" : formatted;
    }
}
