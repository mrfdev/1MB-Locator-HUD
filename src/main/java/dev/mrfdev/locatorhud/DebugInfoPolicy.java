package dev.mrfdev.locatorhud;

import java.util.Objects;

public final class DebugInfoPolicy {
    private DebugInfoPolicy() {
    }

    public static boolean allowsCoordinates(boolean serverReducedDebugInfo) {
        return !serverReducedDebugInfo;
    }

    public static CoordinateDisplayMode coordinateDisplay(
        boolean serverReducedDebugInfo,
        CoordinateDisplayMode configuredDisplay
    ) {
        Objects.requireNonNull(configuredDisplay, "configuredDisplay");
        return allowsCoordinates(serverReducedDebugInfo)
            ? configuredDisplay
            : CoordinateDisplayMode.HIDDEN;
    }

    public static boolean coordinateLensEnabled(
        boolean serverReducedDebugInfo,
        boolean configuredEnabled
    ) {
        return allowsCoordinates(serverReducedDebugInfo) && configuredEnabled;
    }

    public static boolean allowsTargetDetails(boolean serverReducedDebugInfo) {
        return !serverReducedDebugInfo;
    }

    public static boolean targetDetailEnabled(
        boolean serverReducedDebugInfo,
        boolean configuredEnabled
    ) {
        return allowsTargetDetails(serverReducedDebugInfo) && configuredEnabled;
    }
}
