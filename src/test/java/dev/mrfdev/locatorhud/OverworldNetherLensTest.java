package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class OverworldNetherLensTest {
    @Test
    void recognizesOnlyTheTwoVanillaCoordinateSpaces() {
        assertEquals(
            "hud.locatorhud.coordinate_lens.overworld",
            OverworldNetherLens.Destination.OVERWORLD.translationKey()
        );
        assertEquals(
            "hud.locatorhud.coordinate_lens.nether",
            OverworldNetherLens.Destination.NETHER.translationKey()
        );
        assertSame(
            OverworldNetherLens.SourceDimension.OVERWORLD,
            OverworldNetherLens.classify("minecraft", "overworld")
        );
        assertSame(
            OverworldNetherLens.SourceDimension.NETHER,
            OverworldNetherLens.classify("minecraft", "the_nether")
        );
        assertSame(
            OverworldNetherLens.SourceDimension.UNSUPPORTED,
            OverworldNetherLens.classify("minecraft", "the_end")
        );
        assertSame(
            OverworldNetherLens.SourceDimension.UNSUPPORTED,
            OverworldNetherLens.classify("example", "overworld")
        );
    }

    @Test
    void convertsOverworldCoordinatesToTheNetherWithoutDiscardingFractions() {
        OverworldNetherLens.Projection result = OverworldNetherLens.project(
            OverworldNetherLens.SourceDimension.OVERWORLD,
            801.0D,
            -82.0D
        ).orElseThrow();

        assertSame(OverworldNetherLens.Destination.NETHER, result.destination());
        assertEquals(100.125D, result.x());
        assertEquals(-10.25D, result.z());
    }

    @Test
    void convertsNetherCoordinatesToTheOverworldAndPreservesSigns() {
        OverworldNetherLens.Projection result = OverworldNetherLens.project(
            OverworldNetherLens.SourceDimension.NETHER,
            -12.5D,
            25.25D
        ).orElseThrow();

        assertSame(OverworldNetherLens.Destination.OVERWORLD, result.destination());
        assertEquals(-100.0D, result.x());
        assertEquals(202.0D, result.z());
    }

    @Test
    void omitsUnsupportedAndNonFiniteProjections() {
        assertTrue(OverworldNetherLens.project(
            OverworldNetherLens.SourceDimension.UNSUPPORTED,
            1.0D,
            2.0D
        ).isEmpty());
        assertTrue(OverworldNetherLens.project(
            OverworldNetherLens.SourceDimension.OVERWORLD,
            Double.NaN,
            2.0D
        ).isEmpty());
        assertTrue(OverworldNetherLens.project(
            OverworldNetherLens.SourceDimension.NETHER,
            Double.MAX_VALUE,
            2.0D
        ).isEmpty());
    }
}
