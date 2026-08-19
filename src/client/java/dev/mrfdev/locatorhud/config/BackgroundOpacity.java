package dev.mrfdev.locatorhud.config;

public enum BackgroundOpacity {
    OFF("Off (minimal)", 0),
    SOFT("Soft (55%)", 140),
    BALANCED("Balanced (72%)", 184),
    STRONG("Strong (88%)", 224);

    private final String displayName;
    private final int alpha;

    BackgroundOpacity(String displayName, int alpha) {
        this.displayName = displayName;
        this.alpha = alpha;
    }

    public String displayName() {
        return this.displayName;
    }

    public int applyTo(int rgb) {
        return this.alpha << 24 | rgb & 0x00FFFFFF;
    }

    public boolean drawsPanel() {
        return this.alpha > 0;
    }
}
