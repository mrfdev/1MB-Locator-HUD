package dev.mrfdev.locatorhud;

import java.util.Objects;

public record PanelWidthLimits(PanelWidth minimum, PanelWidth maximum) {
    public static final PanelWidthLimits AUTOMATIC = new PanelWidthLimits(
        PanelWidth.AUTO,
        PanelWidth.AUTO
    );

    public PanelWidthLimits {
        Objects.requireNonNull(minimum, "minimum");
        Objects.requireNonNull(maximum, "maximum");
        if (crosses(minimum, maximum)) {
            throw new IllegalArgumentException("minimum panel width must not exceed maximum");
        }
    }

    public static PanelWidthLimits normalized(PanelWidth minimum, PanelWidth maximum) {
        PanelWidth resolvedMinimum = minimum != null ? minimum : PanelWidth.AUTO;
        PanelWidth resolvedMaximum = maximum != null ? maximum : PanelWidth.AUTO;
        if (crosses(resolvedMinimum, resolvedMaximum)) {
            resolvedMinimum = resolvedMaximum;
        }
        return new PanelWidthLimits(resolvedMinimum, resolvedMaximum);
    }

    public PanelWidthLimits withMinimum(PanelWidth minimum) {
        PanelWidth resolvedMinimum = minimum != null ? minimum : PanelWidth.AUTO;
        PanelWidth resolvedMaximum = this.maximum;
        if (crosses(resolvedMinimum, resolvedMaximum)) {
            resolvedMaximum = resolvedMinimum;
        }
        return new PanelWidthLimits(resolvedMinimum, resolvedMaximum);
    }

    public PanelWidthLimits withMaximum(PanelWidth maximum) {
        PanelWidth resolvedMinimum = this.minimum;
        PanelWidth resolvedMaximum = maximum != null ? maximum : PanelWidth.AUTO;
        if (crosses(resolvedMinimum, resolvedMaximum)) {
            resolvedMinimum = resolvedMaximum;
        }
        return new PanelWidthLimits(resolvedMinimum, resolvedMaximum);
    }

    public int constrainContentWidth(
        int naturalContentWidth,
        int screenMaximumContentWidth,
        HudLayout layout
    ) {
        if (naturalContentWidth < 0) {
            throw new IllegalArgumentException("naturalContentWidth must not be negative");
        }
        if (screenMaximumContentWidth < 0) {
            throw new IllegalArgumentException("screenMaximumContentWidth must not be negative");
        }
        Objects.requireNonNull(layout, "layout");

        int chromeWidth = layout.panelWidth(0);
        int configuredMinimum = contentWidth(this.minimum, chromeWidth);
        int configuredMaximum = this.maximum.automatic()
            ? screenMaximumContentWidth
            : contentWidth(this.maximum, chromeWidth);
        int effectiveMaximum = Math.min(screenMaximumContentWidth, configuredMaximum);
        int effectiveMinimum = Math.min(configuredMinimum, effectiveMaximum);
        return Math.max(effectiveMinimum, Math.min(naturalContentWidth, effectiveMaximum));
    }

    private static int contentWidth(PanelWidth width, int chromeWidth) {
        return width.automatic() ? 0 : Math.max(0, width.pixels() - chromeWidth);
    }

    private static boolean crosses(PanelWidth minimum, PanelWidth maximum) {
        return !minimum.automatic()
            && !maximum.automatic()
            && minimum.pixels() > maximum.pixels();
    }
}
