package dev.mrfdev.locatorhud;

public record HudLayout(
    int margin,
    int horizontalPadding,
    int verticalPadding,
    int accentWidth,
    int rowGap,
    int segmentGap,
    boolean drawsPanel
) {
    private static final HudLayout PANEL = new HudLayout(8, 8, 6, 3, 3, 9, true);
    private static final HudLayout MINIMAL = new HudLayout(4, 1, 1, 0, 0, 5, false);

    public static HudLayout forPanel(boolean drawsPanel) {
        return drawsPanel ? PANEL : MINIMAL;
    }

    public int panelWidth(int contentWidth) {
        return contentWidth + this.horizontalPadding * 2 + this.accentWidth;
    }

    public int panelHeight(int lineHeight, int rowCount) {
        return this.verticalPadding * 2 + lineHeight * rowCount + this.rowGap * Math.max(0, rowCount - 1);
    }

    public String detailDivider() {
        return " • ";
    }

    public String coordinateDivider() {
        return this.drawsPanel ? " / " : "/";
    }
}
