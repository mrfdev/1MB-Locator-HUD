package dev.mrfdev.locatorhud;

public enum CoordinateDisplayMode {
    DECIMAL_ONLY("XYZ only", true, false),
    BLOCK_ONLY("Block XYZ only", false, true),
    BOTH("XYZ + block", true, true),
    HIDDEN("None", false, false);

    private final String displayName;
    private final boolean showsDecimal;
    private final boolean showsBlock;

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

    public int coordinateRows() {
        return (this.showsDecimal ? 1 : 0) + (this.showsBlock ? 1 : 0);
    }

    public boolean worldSharesDecimalRow(boolean showWorld) {
        return showWorld && this.showsDecimal;
    }

    public boolean worldSharesBlockRow(boolean showWorld) {
        return showWorld && !this.showsDecimal && this.showsBlock;
    }

    public boolean worldUsesOwnRow(boolean showWorld) {
        return showWorld && !this.showsDecimal && !this.showsBlock;
    }

    public int coreRows(boolean showWorld) {
        return coordinateRows() + (worldUsesOwnRow(showWorld) ? 1 : 0) + 1;
    }
}
