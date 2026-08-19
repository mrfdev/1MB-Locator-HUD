package dev.mrfdev.locatorhud;

public enum PanelWidth {
    AUTO(0),
    PX_120(120),
    PX_160(160),
    PX_200(200),
    PX_240(240),
    PX_280(280),
    PX_320(320);

    private final int pixels;

    PanelWidth(int pixels) {
        this.pixels = pixels;
    }

    public boolean automatic() {
        return this == AUTO;
    }

    public int pixels() {
        return this.pixels;
    }

    public String translationKey() {
        return automatic()
            ? "value.locatorhud.panel_width.auto"
            : "value.locatorhud.panel_width.pixels";
    }
}
