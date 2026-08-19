package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.PanelGeometry.Placement;

/**
 * Latest rendered panel bounds, shared with the explicit placement editor on the client thread.
 */
public final class HudPanelPlacements {
    private Snapshot snapshot = new Snapshot(null, null);

    void update(Placement main, Placement details) {
        this.snapshot = new Snapshot(main, details);
    }

    public Snapshot snapshot() {
        return this.snapshot;
    }

    public record Snapshot(Placement main, Placement details) {
    }
}
