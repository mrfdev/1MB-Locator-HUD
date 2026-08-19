package dev.mrfdev.locatorhud;

import java.util.Locale;

public final class DirectionNameFormatter {
    private DirectionNameFormatter() {
    }

    public static String titleCase(String direction) {
        if (direction == null || direction.isBlank()) {
            return "Unknown";
        }
        return direction.substring(0, 1).toUpperCase(Locale.ROOT)
            + direction.substring(1).toLowerCase(Locale.ROOT);
    }
}
