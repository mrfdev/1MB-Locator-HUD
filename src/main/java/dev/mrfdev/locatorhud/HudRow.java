package dev.mrfdev.locatorhud;

import java.util.List;
import java.util.Objects;

public record HudRow(List<HudRowPart> parts) {
    public HudRow {
        parts = List.copyOf(Objects.requireNonNull(parts, "parts"));
    }

    public static HudRow of(HudRowPart... parts) {
        return new HudRow(List.of(parts));
    }
}
