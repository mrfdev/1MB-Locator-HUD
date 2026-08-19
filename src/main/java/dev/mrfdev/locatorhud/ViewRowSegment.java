package dev.mrfdev.locatorhud;

import java.util.List;

public enum ViewRowSegment {
    DIRECTION,
    OPEN_PARENTHESIS,
    HORIZONTAL_ANGLE,
    DIVIDER,
    VERTICAL_ANGLE,
    CLOSE_PARENTHESIS;

    private static final List<ViewRowSegment> HIDDEN = List.of();
    private static final List<ViewRowSegment> DIRECTION_ONLY = List.of(DIRECTION);
    private static final List<ViewRowSegment> ANGLES_ONLY = List.of(
        HORIZONTAL_ANGLE,
        DIVIDER,
        VERTICAL_ANGLE
    );
    private static final List<ViewRowSegment> DIRECTION_AND_ANGLES = List.of(
        DIRECTION,
        OPEN_PARENTHESIS,
        HORIZONTAL_ANGLE,
        DIVIDER,
        VERTICAL_ANGLE,
        CLOSE_PARENTHESIS
    );

    public static List<ViewRowSegment> forSettings(boolean directionEnabled, boolean anglesEnabled) {
        if (directionEnabled && anglesEnabled) {
            return DIRECTION_AND_ANGLES;
        }
        if (directionEnabled) {
            return DIRECTION_ONLY;
        }
        if (anglesEnabled) {
            return ANGLES_ONLY;
        }
        return HIDDEN;
    }
}
