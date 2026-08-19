package dev.mrfdev.locatorhud;

public final class ViewAngleFormatter {
    private ViewAngleFormatter() {
    }

    public static String horizontal(float yaw) {
        return horizontal(yaw, ViewAnglePrecision.WHOLE);
    }

    public static String horizontal(float yaw, ViewAnglePrecision precision) {
        return precision.formatDegrees(wrapDegrees(yaw));
    }

    public static String vertical(float pitch) {
        return vertical(pitch, ViewAnglePrecision.WHOLE);
    }

    public static String vertical(float pitch, ViewAnglePrecision precision) {
        float wrapped = wrapDegrees(pitch);
        float rounded = precision.round(wrapped);
        if (rounded == 0.0F) {
            return precision.formatDegrees(0.0F) + " level";
        }
        return precision.formatDegrees(Math.abs(rounded)) + " " + (rounded > 0.0F ? "down" : "up");
    }

    static float wrapDegrees(float degrees) {
        float wrapped = degrees % 360.0F;
        if (wrapped >= 180.0F) {
            wrapped -= 360.0F;
        }
        if (wrapped < -180.0F) {
            wrapped += 360.0F;
        }
        return wrapped;
    }
}
