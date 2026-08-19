package dev.mrfdev.locatorhud;

import java.util.Locale;

public enum CoordinatePrecision {
    BLOCK("Block", 0),
    ONE_DECIMAL("1 decimal", 1),
    TWO_DECIMALS("2 decimals", 2);

    private final String displayName;
    private final int decimalPlaces;

    CoordinatePrecision(String displayName, int decimalPlaces) {
        this.displayName = displayName;
        this.decimalPlaces = decimalPlaces;
    }

    public String displayName() {
        return this.displayName;
    }

    public String format(double coordinate) {
        if (!Double.isFinite(coordinate)) {
            return "?";
        }
        if (this.decimalPlaces == 0) {
            return Long.toString((long) Math.floor(coordinate));
        }
        return String.format(Locale.ROOT, "%." + this.decimalPlaces + "f", coordinate);
    }
}
