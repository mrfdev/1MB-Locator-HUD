package dev.mrfdev.locatorhud;

import java.util.List;

public enum CoordinateDisplayMode {
    DECIMAL_ONLY("XYZ only", true, false),
    BLOCK_ONLY("Block XYZ only", false, true),
    BOTH("XYZ + block", true, true),
    HIDDEN("None", false, false);

    private final String displayName;
    private final boolean showsDecimal;
    private final boolean showsBlock;
    private static final List<CoordinateRowSegment> NO_SEGMENTS = List.of();
    private static final List<CoordinateRowSegment> COORDINATES_ONLY = List.of(
        CoordinateRowSegment.COORDINATES
    );
    private static final List<CoordinateRowSegment> WORLD_THEN_COORDINATES = List.of(
        CoordinateRowSegment.WORLD,
        CoordinateRowSegment.COORDINATES
    );
    private static final List<CoordinateRowSegment> COORDINATES_THEN_WORLD = List.of(
        CoordinateRowSegment.COORDINATES,
        CoordinateRowSegment.WORLD
    );

    CoordinateDisplayMode(String displayName, boolean showsDecimal, boolean showsBlock) {
        this.displayName = displayName;
        this.showsDecimal = showsDecimal;
        this.showsBlock = showsBlock;
    }

    public String displayName() {
        return this.displayName;
    }

    public boolean showsDecimal() {
        return this.showsDecimal;
    }

    public boolean showsBlock() {
        return this.showsBlock;
    }

    public boolean worldSharesDecimalRow(WorldNameDisplay worldDisplay) {
        return worldDisplay.showsWorld() && this.showsDecimal;
    }

    public boolean worldSharesBlockRow(WorldNameDisplay worldDisplay) {
        return worldDisplay.showsWorld() && !this.showsDecimal && this.showsBlock;
    }

    public boolean worldUsesOwnRow(WorldNameDisplay worldDisplay) {
        return worldDisplay.showsWorld() && !this.showsDecimal && !this.showsBlock;
    }

    public List<CoordinateRowSegment> decimalRowSegments(WorldNameDisplay worldDisplay) {
        return this.showsDecimal
            ? coordinateRowSegments(worldDisplay, worldSharesDecimalRow(worldDisplay))
            : NO_SEGMENTS;
    }

    public List<CoordinateRowSegment> blockRowSegments(WorldNameDisplay worldDisplay) {
        return this.showsBlock
            ? coordinateRowSegments(worldDisplay, worldSharesBlockRow(worldDisplay))
            : NO_SEGMENTS;
    }

    private static List<CoordinateRowSegment> coordinateRowSegments(
        WorldNameDisplay worldDisplay,
        boolean sharesWorld
    ) {
        if (!sharesWorld) {
            return COORDINATES_ONLY;
        }
        return worldDisplay.beforeCoordinates() ? WORLD_THEN_COORDINATES : COORDINATES_THEN_WORLD;
    }
}
