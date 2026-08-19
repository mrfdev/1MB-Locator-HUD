package dev.mrfdev.locatorhud;

public record DetailsRowVisibility(
    boolean biome,
    boolean targetBlock,
    boolean targetFluid,
    boolean targetEntity
) {
    public static DetailsRowVisibility resolve(
        boolean biomeEnabled,
        boolean targetBlockEnabled,
        String targetBlock,
        boolean targetFluidEnabled,
        String targetFluid,
        boolean targetEntityEnabled,
        String targetEntity,
        boolean autoHideEmptyValues
    ) {
        return new DetailsRowVisibility(
            biomeEnabled,
            targetVisible(targetBlockEnabled, targetBlock, autoHideEmptyValues),
            targetVisible(targetFluidEnabled, targetFluid, autoHideEmptyValues),
            targetVisible(targetEntityEnabled, targetEntity, autoHideEmptyValues)
        );
    }

    public int rowCount() {
        return (this.biome ? 1 : 0)
            + (this.targetBlock ? 1 : 0)
            + (this.targetFluid ? 1 : 0)
            + (this.targetEntity ? 1 : 0);
    }

    public boolean isEmpty() {
        return rowCount() == 0;
    }

    private static boolean targetVisible(boolean enabled, String value, boolean autoHideEmptyValues) {
        return enabled && (!autoHideEmptyValues || TargetValue.hasValue(value));
    }
}
