package dev.mrfdev.locatorhud;

public enum HudScale {
    COMPACT("Compact (80%)", 0.8F),
    SMALL("Small (90%)", 0.9F),
    NORMAL("Normal (100%)", 1.0F);

    private final String displayName;
    private final float factor;

    HudScale(String displayName, float factor) {
        this.displayName = displayName;
        this.factor = factor;
    }

    public String displayName() {
        return this.displayName;
    }

    public float factor() {
        return this.factor;
    }

    public int scaleDimension(int unscaledDimension) {
        return (int) Math.ceil(unscaledDimension * this.factor);
    }
}
