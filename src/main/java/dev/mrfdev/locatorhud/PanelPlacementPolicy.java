package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.PanelGeometry.Offset;
import dev.mrfdev.locatorhud.PanelGeometry.PanelSize;
import dev.mrfdev.locatorhud.PanelGeometry.Placement;
import dev.mrfdev.locatorhud.PanelGeometry.Screen;
import dev.mrfdev.locatorhud.config.HudCorner;
import java.util.Objects;

/**
 * Converts a dragged panel position into a corner anchor and a small, stable offset.
 */
public final class PanelPlacementPolicy {
    public static final int MAXIMUM_OFFSET = 64;
    public static final int SNAP_DISTANCE = 6;

    private PanelPlacementPolicy() {
    }

    public static Result resolve(
        Screen screen,
        HudLayout layout,
        PanelSize size,
        int desiredX,
        int desiredY
    ) {
        Objects.requireNonNull(screen, "screen");
        Objects.requireNonNull(layout, "layout");
        Objects.requireNonNull(size, "size");

        HudCorner corner = closestCorner(screen, size, desiredX, desiredY);
        Placement anchor = PanelGeometry.place(screen, corner, layout, size);
        Offset requestedOffset = new Offset(
            normalizeOffset((long) desiredX - anchor.x()),
            normalizeOffset((long) desiredY - anchor.y())
        );
        Placement placement = PanelGeometry.place(screen, corner, layout, size, requestedOffset);
        Offset effectiveOffset = new Offset(
            placement.x() - anchor.x(),
            placement.y() - anchor.y()
        );
        return new Result(corner, effectiveOffset, placement);
    }

    public static Offset clampOffset(Offset offset) {
        Objects.requireNonNull(offset, "offset");
        return new Offset(clamp(offset.x()), clamp(offset.y()));
    }

    private static HudCorner closestCorner(
        Screen screen,
        PanelSize size,
        int desiredX,
        int desiredY
    ) {
        boolean right = (long) desiredX * 2 + size.scaledWidth() >= screen.width();
        boolean bottom = (long) desiredY * 2 + size.scaledHeight() >= screen.height();
        if (bottom) {
            return right ? HudCorner.BOTTOM_RIGHT : HudCorner.BOTTOM_LEFT;
        }
        return right ? HudCorner.TOP_RIGHT : HudCorner.TOP_LEFT;
    }

    private static int normalizeOffset(long value) {
        if (Math.abs(value) <= SNAP_DISTANCE) {
            return 0;
        }
        return (int) Math.max(-MAXIMUM_OFFSET, Math.min(value, MAXIMUM_OFFSET));
    }

    private static int clamp(int value) {
        return Math.max(-MAXIMUM_OFFSET, Math.min(value, MAXIMUM_OFFSET));
    }

    public record Result(HudCorner corner, Offset offset, Placement placement) {
        public Result {
            Objects.requireNonNull(corner, "corner");
            Objects.requireNonNull(offset, "offset");
            Objects.requireNonNull(placement, "placement");
        }
    }
}
