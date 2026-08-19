package dev.mrfdev.locatorhud;

public enum HudScale {
    EXTRA_SMALL("Extra small (60%)", 60),
    VERY_SMALL("Very small (70%)", 70),
    COMPACT("Compact (80%)", 80),
    SMALL("Small (90%)", 90),
    NORMAL("Normal (100%)", 100);

    private static final int MINIMUM_PERCENTAGE = 60;
    private static final int PERCENTAGE_RANGE = 40;

    private final String displayName;
    private final int percentage;

    HudScale(String displayName, int percentage) {
        this.displayName = displayName;
        this.percentage = percentage;
    }

    public String displayName() {
        return this.displayName;
    }

    public int percentage() {
        return this.percentage;
    }

    public float factor() {
        return this.percentage / 100.0F;
    }

    public int scaleDimension(int unscaledDimension) {
        return (unscaledDimension * this.percentage + 99) / 100;
    }

    public double sliderPosition() {
        return (this.percentage - MINIMUM_PERCENTAGE) / (double) PERCENTAGE_RANGE;
    }

    public static HudScale nearestSliderPosition(double position) {
        double clampedPosition = Math.max(0.0, Math.min(1.0, position));
        HudScale nearest = EXTRA_SMALL;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (HudScale candidate : values()) {
            double distance = Math.abs(candidate.sliderPosition() - clampedPosition);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }
        return nearest;
    }
}
