package dev.mrfdev.locatorhud;

import java.util.Locale;

public enum ViewAnglePrecision {
    WHOLE("options.off", 0),
    ONE_DECIMAL("value.locatorhud.view_angle_precision.one_decimal", 1),
    TWO_DECIMALS("value.locatorhud.view_angle_precision.two_decimals", 2);

    private final String translationKey;
    private final int decimalPlaces;
    private final float factor;

    ViewAnglePrecision(String translationKey, int decimalPlaces) {
        this.translationKey = translationKey;
        this.decimalPlaces = decimalPlaces;
        this.factor = (float) Math.pow(10, decimalPlaces);
    }

    public String translationKey() {
        return this.translationKey;
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
