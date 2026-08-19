package dev.mrfdev.locatorhud;

public final class HudSampleSchedule {
    private static final int BIOME_SAMPLE_INTERVAL_TICKS = 20;
    private static final int TARGET_SAMPLE_INTERVAL_TICKS = 2;

    private long tick;
    private long nextBiomeSampleTick;
    private long nextTargetSampleTick;
    private boolean biomePreviouslyRequested;
    private boolean hasBiomePosition;
    private int biomeX;
    private int biomeY;
    private int biomeZ;
    private int previousTargetSelection;

    public Refresh advance(
        boolean biomeRequested,
        int blockX,
        int blockY,
        int blockZ,
        int targetSelection
    ) {
        this.tick++;

        boolean biomePositionChanged = !this.hasBiomePosition
            || blockX != this.biomeX
            || blockY != this.biomeY
            || blockZ != this.biomeZ;
        boolean sampleBiome = biomeRequested
            && (!this.biomePreviouslyRequested
                || biomePositionChanged
                || this.tick >= this.nextBiomeSampleTick);
        if (sampleBiome) {
            this.hasBiomePosition = true;
            this.biomeX = blockX;
            this.biomeY = blockY;
            this.biomeZ = blockZ;
            this.nextBiomeSampleTick = this.tick + BIOME_SAMPLE_INTERVAL_TICKS;
        }

        boolean targetsRequested = targetSelection != 0;
        boolean sampleTargets = targetsRequested
            && (targetSelection != this.previousTargetSelection
                || this.tick >= this.nextTargetSampleTick);
        if (sampleTargets) {
            this.nextTargetSampleTick = this.tick + TARGET_SAMPLE_INTERVAL_TICKS;
        }

        this.biomePreviouslyRequested = biomeRequested;
        this.previousTargetSelection = targetSelection;
        return new Refresh(sampleBiome, sampleTargets);
    }

    public void reset() {
        this.tick = 0;
        this.nextBiomeSampleTick = 0;
        this.nextTargetSampleTick = 0;
        this.biomePreviouslyRequested = false;
        this.hasBiomePosition = false;
        this.previousTargetSelection = 0;
    }

    public record Refresh(boolean biome, boolean targets) {
    }
}
