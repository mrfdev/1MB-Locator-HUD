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

    public int x(int screenWidth, int panelWidth, int margin) {
        return this == TOP_RIGHT || this == BOTTOM_RIGHT ? screenWidth - panelWidth - margin : margin;
    }

    public int y(int screenHeight, int panelHeight, int margin) {
        return this == BOTTOM_LEFT || this == BOTTOM_RIGHT ? screenHeight - panelHeight - margin : margin;
    }

    public boolean isTop() {
        return this == TOP_LEFT || this == TOP_RIGHT;
    }
}
