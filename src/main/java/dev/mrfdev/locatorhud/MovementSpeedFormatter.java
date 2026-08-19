package dev.mrfdev.locatorhud;

import java.util.Locale;

public final class MovementSpeedFormatter {
    private MovementSpeedFormatter() {
    }

    public static String format(double blocksPerSecond) {
        if (!Double.isFinite(blocksPerSecond) || blocksPerSecond < 0.0D) {
            return "? b/s";
        }
        return String.format(Locale.ROOT, "%.1f b/s", blocksPerSecond);
    }
}
