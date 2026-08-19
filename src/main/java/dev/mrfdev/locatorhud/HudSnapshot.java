package dev.mrfdev.locatorhud;

import java.util.Objects;
import java.util.Optional;

public record HudSnapshot(
    double x,
    double y,
    double z,
    String directionName,
    float yaw,
    float pitch,
    String world,
    OverworldNetherLens.SourceDimension sourceDimension,
    String biome,
    Optional<BiomeTransitionTracker.Notice> biomeTransition,
    double movementSpeed,
    HudPaletteColors paletteColors,
    CrosshairTargets targets
) {
    public HudSnapshot {
        Objects.requireNonNull(directionName, "directionName");
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(sourceDimension, "sourceDimension");
        Objects.requireNonNull(biome, "biome");
        Objects.requireNonNull(biomeTransition, "biomeTransition");
        Objects.requireNonNull(paletteColors, "paletteColors");
        Objects.requireNonNull(targets, "targets");
        if (!Double.isFinite(movementSpeed) || movementSpeed < 0.0D) {
            throw new IllegalArgumentException("movementSpeed must be finite and non-negative");
        }
    }
}
