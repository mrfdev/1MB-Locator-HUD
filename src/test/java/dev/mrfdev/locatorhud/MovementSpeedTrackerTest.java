package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.mrfdev.locatorhud.config.ColorPalette;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class MovementSpeedTrackerTest {
    @Test
    void smoothsHorizontalMovementAcrossAHalfSecondWindow() {
        MovementSpeedTracker tracker = new MovementSpeedTracker();
        assertEquals(0.0D, tracker.update(0.0D, 0.0D));

        for (int tick = 1; tick <= MovementSpeedTracker.WINDOW_TICKS; tick++) {
            double expected = tick * 0.4D;
            assertEquals(expected, tracker.update(tick * 0.2D, 0.0D), 0.000_001D);
        }
        assertEquals(4.0D, tracker.update(2.2D, 0.0D), 0.000_001D);
    }

    @Test
    void ignoresVerticalMovementAndResetsLargePositionCorrections() {
        MovementSpeedTracker tracker = new MovementSpeedTracker();
        tracker.update(0.0D, 0.0D);
        assertEquals(0.0D, tracker.update(0.0D, 0.0D));
        assertEquals(0.0D, tracker.update(17.0D, 0.0D));
        assertEquals(0.4D, tracker.update(17.2D, 0.0D), 0.000_001D);
    }

    @Test
    void resetsInvalidSamplesAndFormatsValuesCompactly() {
        MovementSpeedTracker tracker = new MovementSpeedTracker();
        tracker.update(0.0D, 0.0D);
        assertEquals(0.0D, tracker.update(Double.NaN, 0.0D));
        assertEquals("4.3 b/s", MovementSpeedFormatter.format(4.25D));
        assertEquals("? b/s", MovementSpeedFormatter.format(Double.POSITIVE_INFINITY));
        assertEquals("? b/s", MovementSpeedFormatter.format(-1.0D));
    }

    @Test
    void neverReturnsNegativeRoundingResidueAfterMovementStops() {
        MovementSpeedTracker tracker = new MovementSpeedTracker();
        double[] distances = {
            3.077612025071463E-4,
            3.826172858885419E-6,
            0.00291075510240385,
            0.007130148681508228,
            8.532079790457816E-6,
            2.2686541600512393E-6,
            2.677415486971443E-4,
            9.887079056168774E-5,
            3.4503977928221197E-6,
            6.95810513406852E-7
        };
        double x = 0.0D;
        tracker.update(x, 0.0D);
        for (double distance : distances) {
            x += distance;
            tracker.update(x, 0.0D);
        }

        double stoppedSpeed = 0.0D;
        for (int tick = 0; tick < MovementSpeedTracker.WINDOW_TICKS; tick++) {
            stoppedSpeed = tracker.update(x, 0.0D);
        }

        assertTrue(stoppedSpeed >= 0.0D, "snapshot input must never be negative");
        assertEquals(0.0D, stoppedSpeed);
        double snapshotX = x;
        double snapshotSpeed = stoppedSpeed;
        assertDoesNotThrow(() -> new HudSnapshot(
            snapshotX,
            64.0D,
            0.0D,
            false,
            "north",
            0.0F,
            0.0F,
            "Overworld",
            OverworldNetherLens.SourceDimension.OVERWORLD,
            "Plains",
            Optional.empty(),
            snapshotSpeed,
            ColorPalette.OCEAN.colors(),
            CrosshairTargets.empty()
        ));
    }
}
