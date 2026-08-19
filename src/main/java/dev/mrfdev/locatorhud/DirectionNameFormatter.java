package dev.mrfdev.locatorhud;

import java.util.Locale;

public final class DirectionNameFormatter {
    private static final String[] DETAILED_DIRECTIONS = {
        "South [+Z]",
        "Southwest [-X/+Z]",
        "West [-X]",
        "Northwest [-X/-Z]",
        "North [-Z]",
        "Northeast [+X/-Z]",
        "East [+X]",
        "Southeast [+X/+Z]"
    };

    private DirectionNameFormatter() {
    }

    public static String titleCase(String direction) {
        if (direction == null || direction.isBlank()) {
            return "Unknown";
        }
        return direction.substring(0, 1).toUpperCase(Locale.ROOT)
            + direction.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String format(ViewDirectionDisplay display, String cardinalDirection, float yaw) {
        return switch (display) {
            case ON -> titleCase(cardinalDirection);
            case WITH_DETAILS -> detailed(yaw);
            case OFF -> "";
        };
    }

    public static String detailed(float yaw) {
        if (!Float.isFinite(yaw)) {
            return "Unknown";
        }
        float normalizedYaw = yaw % 360.0F;
        int index = Math.floorMod((int) Math.floor(normalizedYaw / 45.0F + 0.5F), 8);
        return DETAILED_DIRECTIONS[index];
    }
}
