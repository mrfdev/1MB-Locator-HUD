package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.config.ColorPalette;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class HudSnapshotTest {
    @Test
    void retainsOnlyImmutableDisplayData() {
        CrosshairTargets targets = new CrosshairTargets("stone", "water", "cow");
        HudSnapshot snapshot = new HudSnapshot(
            1.25D,
            64.0D,
            -3.5D,
            true,
            "north",
            10.0F,
            -5.0F,
            "Overworld",
            OverworldNetherLens.SourceDimension.OVERWORLD,
            "Plains",
            Optional.empty(),
            4.25D,
            ColorPalette.OCEAN.colors(),
            targets
        );

        assertSame(targets, snapshot.targets());
        assertTrue(snapshot.reducedDebugInfo());
    }

    @Test
    void rejectsNullDisplayValuesAtTheSnapshotBoundary() {
        assertThrows(
            NullPointerException.class,
            () -> new HudSnapshot(
                0.0D,
                0.0D,
                0.0D,
                false,
                "north",
                0.0F,
                0.0F,
                "Overworld",
                OverworldNetherLens.SourceDimension.OVERWORLD,
                null,
                Optional.empty(),
                0.0D,
                ColorPalette.OCEAN.colors(),
                CrosshairTargets.empty()
            )
        );
    }

    @Test
    void rejectsInvalidMovementSpeedAtTheSnapshotBoundary() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new HudSnapshot(
                0.0D,
                0.0D,
                0.0D,
                false,
                "north",
                0.0F,
                0.0F,
                "Overworld",
                OverworldNetherLens.SourceDimension.OVERWORLD,
                "Plains",
                Optional.empty(),
                Double.NaN,
                ColorPalette.OCEAN.colors(),
                CrosshairTargets.empty()
            )
        );
    }
}
