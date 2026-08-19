package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.HudCorner;
import java.util.Objects;

public final class PanelGeometry {
    private static final int NORMAL_SCALE_PERCENTAGE = 100;

    private PanelGeometry() {
    }

    public static int maximumContentWidth(
        Screen screen,
        HudLayout layout,
        int scalePercentage
    ) {
        Objects.requireNonNull(screen, "screen");
        Objects.requireNonNull(layout, "layout");
        requirePositiveScale(scalePercentage);

        int usableScreenWidth = Math.max(0, screen.width() - layout.margin() * 2);
        int maximumPanelWidth = scalePercentage <= NORMAL_SCALE_PERCENTAGE
            ? usableScreenWidth
            : (int) ((long) usableScreenWidth * NORMAL_SCALE_PERCENTAGE / scalePercentage);
        int panelChromeWidth = layout.horizontalPadding() * 2 + layout.accentWidth();
        return Math.max(0, maximumPanelWidth - panelChromeWidth);
    }

    public static PanelSize measure(
        HudLayout layout,
        int contentWidth,
        int lineHeight,
        int rowCount,
        int scalePercentage
    ) {
        Objects.requireNonNull(layout, "layout");
        if (contentWidth < 0) {
            throw new IllegalArgumentException("contentWidth must not be negative");
        }
        if (lineHeight < 0) {
            throw new IllegalArgumentException("lineHeight must not be negative");
        }
        if (rowCount < 0) {
            throw new IllegalArgumentException("rowCount must not be negative");
        }
        requirePositiveScale(scalePercentage);

        int unscaledWidth = layout.panelWidth(contentWidth);
        int unscaledHeight = layout.panelHeight(lineHeight, rowCount);
        return new PanelSize(
            unscaledWidth,
            unscaledHeight,
            scaleDimension(unscaledWidth, scalePercentage),
            scaleDimension(unscaledHeight, scalePercentage)
        );
    }

    public static Placement place(
        Screen screen,
        HudCorner corner,
        HudLayout layout,
        PanelSize size
    ) {
        return place(screen, corner, layout, size, Offset.ZERO);
    }

    public static Placement place(
        Screen screen,
        HudCorner corner,
        HudLayout layout,
        PanelSize size,
        Offset offset
    ) {
        Objects.requireNonNull(screen, "screen");
        Objects.requireNonNull(corner, "corner");
        Objects.requireNonNull(layout, "layout");
        Objects.requireNonNull(size, "size");
        Objects.requireNonNull(offset, "offset");

        boolean right = corner == HudCorner.TOP_RIGHT || corner == HudCorner.BOTTOM_RIGHT;
        boolean bottom = corner == HudCorner.BOTTOM_LEFT || corner == HudCorner.BOTTOM_RIGHT;
        int x = cornerPosition(
            screen.width(),
            size.scaledWidth(),
            layout.margin(),
            right,
            offset.x()
        );
        int y = cornerPosition(
            screen.height(),
            size.scaledHeight(),
            layout.margin(),
            bottom,
            offset.y()
        );
        return new Placement(x, y, size.scaledWidth(), size.scaledHeight());
    }

    public static Placement stack(
        Screen screen,
        Placement details,
        Placement main,
        HudLayout detailsLayout,
        HudCorner corner,
        int gap
    ) {
        Objects.requireNonNull(screen, "screen");
        Objects.requireNonNull(details, "details");
        Objects.requireNonNull(main, "main");
        Objects.requireNonNull(detailsLayout, "detailsLayout");
        Objects.requireNonNull(corner, "corner");
        if (gap < 0) {
            throw new IllegalArgumentException("gap must not be negative");
        }

        boolean bottom = corner == HudCorner.BOTTOM_LEFT || corner == HudCorner.BOTTOM_RIGHT;
        long desiredY = bottom
            ? (long) main.y() - details.height() - gap
            : (long) main.y() + main.height() + gap;
        int y = clampPosition(
            desiredY,
            screen.height(),
            details.height(),
            detailsLayout.margin(),
            bottom
        );
        return new Placement(details.x(), y, details.width(), details.height());
    }

    private static int cornerPosition(
        int screenDimension,
        int panelDimension,
        int margin,
        boolean trailingEdge,
        int offset
    ) {
        long anchor = trailingEdge
            ? (long) screenDimension - panelDimension - margin
            : margin;
        return clampPosition(
            anchor + offset,
            screenDimension,
            panelDimension,
            margin,
            trailingEdge
        );
    }

    private static int clampPosition(
        long desired,
        int screenDimension,
        int panelDimension,
        int margin,
        boolean trailingEdge
    ) {
        if ((long) panelDimension + margin * 2L <= screenDimension) {
            return clamp(desired, margin, screenDimension - panelDimension - margin);
        }
        if (panelDimension <= screenDimension) {
            return clamp(desired, 0, screenDimension - panelDimension);
        }
        return trailingEdge ? screenDimension - panelDimension : 0;
    }

    private static int clamp(long value, int minimum, int maximum) {
        return (int) Math.max(minimum, Math.min(value, maximum));
    }

    private static int scaleDimension(int unscaledDimension, int scalePercentage) {
        long scaled = ((long) unscaledDimension * scalePercentage + 99) / 100;
        if (scaled > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("scaled panel dimension is too large");
        }
        return (int) scaled;
    }

    private static void requirePositiveScale(int scalePercentage) {
        if (scalePercentage <= 0) {
            throw new IllegalArgumentException("scalePercentage must be positive");
        }
    }

    public record Screen(int width, int height) {
        public Screen {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("screen dimensions must not be negative");
            }
        }
    }

    public record PanelSize(
        int unscaledWidth,
        int unscaledHeight,
        int scaledWidth,
        int scaledHeight
    ) {
        public PanelSize {
            if (unscaledWidth < 0 || unscaledHeight < 0 || scaledWidth < 0 || scaledHeight < 0) {
                throw new IllegalArgumentException("panel dimensions must not be negative");
            }
        }
    }

    public record Offset(int x, int y) {
        public static final Offset ZERO = new Offset(0, 0);
    }

    public record Placement(int x, int y, int width, int height) {
        public Placement {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("placement dimensions must not be negative");
            }
        }
    }
}
