package dev.mrfdev.locatorhud.config;

public enum HudCorner {
    TOP_LEFT("value.locatorhud.corner.top_left"),
    TOP_RIGHT("value.locatorhud.corner.top_right"),
    BOTTOM_LEFT("value.locatorhud.corner.bottom_left"),
    BOTTOM_RIGHT("value.locatorhud.corner.bottom_right");

    private final String translationKey;

    HudCorner(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return this.translationKey;
    }
}
