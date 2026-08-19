package dev.mrfdev.locatorhud.config;

public enum BackgroundOpacity {
    OFF("value.locatorhud.background_opacity.off", 0),
    FAINT("value.locatorhud.background_opacity.faint", 7),
    LIGHT("value.locatorhud.background_opacity.light", 24),
    SOFT("value.locatorhud.background_opacity.soft", 55),
    BALANCED("value.locatorhud.background_opacity.balanced", 72),
    STRONG("value.locatorhud.background_opacity.strong", 88),
    SOLID("value.locatorhud.background_opacity.solid", 100);

    private final String translationKey;
    private final int percentage;

    BackgroundOpacity(String translationKey, int percentage) {
        this.translationKey = translationKey;
        this.percentage = percentage;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public int percentage() {
        return this.percentage;
    }

    public int alpha() {
        return (this.percentage * 255 + 50) / 100;
    }

    public int applyTo(int rgb) {
        return alpha() << 24 | rgb & 0x00FFFFFF;
    }

    public boolean drawsPanel() {
        return this.percentage > 0;
    }

    public double sliderPosition() {
        return this.percentage / 100.0;
    }
}
