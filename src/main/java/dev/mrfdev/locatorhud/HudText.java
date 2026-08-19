package dev.mrfdev.locatorhud;

import java.util.Objects;

public record HudText(String text, HudTextRole role, boolean truncatable) implements HudRowPart {
    public HudText {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(role, "role");
    }

    public static HudText of(String text, HudTextRole role) {
        return new HudText(text, role, false);
    }

    public static HudText truncatable(String text, HudTextRole role) {
        return new HudText(text, role, true);
    }

    public HudText withText(String text) {
        return new HudText(text, this.role, this.truncatable);
    }
}
