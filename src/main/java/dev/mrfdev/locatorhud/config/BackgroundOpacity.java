package dev.mrfdev.locatorhud.config;

public enum BackgroundOpacity {
    OFF("OFF (minimal)", 0),
    FAINT("Faint (7%)", 7),
    LIGHT("Light (24%)", 24),
    SOFT("Soft (55%)", 55),
    BALANCED("Balanced (72%)", 72),
    STRONG("Strong (88%)", 88),
    SOLID("Solid (100%)", 100);

    private final String displayName;
    private final int percentage;

    BackgroundOpacity(String displayName, int percentage) {
        this.displayName = displayName;
        this.percentage = percentage;
    }

    public String displayName() {
        return this.displayName;
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
