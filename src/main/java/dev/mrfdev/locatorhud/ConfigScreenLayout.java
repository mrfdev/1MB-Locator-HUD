package dev.mrfdev.locatorhud;

import java.util.ArrayList;
import java.util.List;

public final class ConfigScreenLayout {
    public static final int WIDE_BUTTON_WIDTH = 240;
    public static final int MAXIMUM_SINGLE_COLUMN_WIDTH = 320;
    public static final int COLUMN_GAP = 8;
    public static final int SCROLLBAR_RESERVE = 28;
    public static final int OUTER_MARGIN = 12;
    public static final int TWO_COLUMN_THRESHOLD = WIDE_BUTTON_WIDTH * 2
        + COLUMN_GAP
        + SCROLLBAR_RESERVE
        + OUTER_MARGIN;

    private ConfigScreenLayout() {
    }

    public static Plan forScreenWidth(int screenWidth) {
        if (screenWidth < 0) {
            throw new IllegalArgumentException("screenWidth must not be negative");
        }
        if (screenWidth >= TWO_COLUMN_THRESHOLD) {
            return new Plan(true, WIDE_BUTTON_WIDTH, WIDE_BUTTON_WIDTH * 2 + COLUMN_GAP);
        }
        int buttonWidth = Math.max(
            1,
            Math.min(MAXIMUM_SINGLE_COLUMN_WIDTH, screenWidth - SCROLLBAR_RESERVE - OUTER_MARGIN)
        );
        return new Plan(false, buttonWidth, buttonWidth);
    }

    public static boolean pairsPanelSliders(int panelWidth) {
        return panelWidth >= WIDE_BUTTON_WIDTH;
    }

    public static List<Integer> equalColumnWidths(int totalWidth, int columns, int gap) {
        if (totalWidth <= 0) {
            throw new IllegalArgumentException("totalWidth must be positive");
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("columns must be positive");
        }
        if (gap < 0) {
            throw new IllegalArgumentException("gap must not be negative");
        }

        long availableWidth = (long) totalWidth - (long) (columns - 1) * gap;
        if (availableWidth < columns) {
            throw new IllegalArgumentException("each column must have at least one pixel");
        }

        int baseWidth = (int) (availableWidth / columns);
        int remainder = (int) (availableWidth % columns);
        List<Integer> widths = new ArrayList<>(columns);
        for (int column = 0; column < columns; column++) {
            widths.add(baseWidth + (column < remainder ? 1 : 0));
        }
        return List.copyOf(widths);
    }

    public record Plan(boolean twoColumns, int buttonWidth, int contentWidth) {
        public Plan {
            if (buttonWidth <= 0 || contentWidth <= 0) {
                throw new IllegalArgumentException("layout widths must be positive");
            }
        }
    }
}
