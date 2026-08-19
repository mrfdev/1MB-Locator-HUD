package dev.mrfdev.locatorhud;

public record HudGap(int width) implements HudRowPart {
    public HudGap {
        if (width < 0) {
            throw new IllegalArgumentException("width must not be negative");
        }
    }
}
