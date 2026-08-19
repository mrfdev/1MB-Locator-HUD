package dev.mrfdev.locatorhud;

import java.util.Locale;

public enum ViewAnglePrecision {
    WHOLE("OFF", 0),
    ONE_DECIMAL("1 decimal", 1),
    TWO_DECIMALS("2 decimals", 2);

    private final String displayName;
    private final int decimalPlaces;
    private final float factor;

    ViewAnglePrecision(String displayName, int decimalPlaces) {
        this.displayName = displayName;
        this.decimalPlaces = decimalPlaces;
        this.factor = (float) Math.pow(10, decimalPlaces);
    }

    public String displayName() {
        return this.displayName;
    }

    public float round(float degrees) {
        float rounded = Math.round(degrees * this.factor) / this.factor;
        return rounded == 0.0F ? 0.0F : rounded;
    }

    public String formatDegrees(float degrees) {
        float rounded = round(degrees);
        return switch (this.decimalPlaces) {
            case 0 -> Math.round(rounded) + "°";
            case 1 -> String.format(Locale.ROOT, "%.1f°", rounded);
            case 2 -> String.format(Locale.ROOT, "%.2f°", rounded);
            default -> throw new IllegalStateException("Unsupported angle precision: " + this.decimalPlaces);
        };
    }
}
