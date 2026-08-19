package dev.mrfdev.locatorhud;

import java.util.List;
import java.util.Objects;

public record HudPanelContent(List<HudRow> rows) {
    public HudPanelContent {
        rows = List.copyOf(Objects.requireNonNull(rows, "rows"));
    }

    public boolean isEmpty() {
        return this.rows.isEmpty();
    }

    public int rowCount() {
        return this.rows.size();
    }
}
