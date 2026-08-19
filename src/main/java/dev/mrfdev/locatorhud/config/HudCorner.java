package dev.mrfdev.locatorhud.config;

public enum HudCorner {
    TOP_LEFT("Top left"),
    TOP_RIGHT("Top right"),
    BOTTOM_LEFT("Bottom left"),
    BOTTOM_RIGHT("Bottom right");

    private final String displayName;

    HudCorner(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

}
