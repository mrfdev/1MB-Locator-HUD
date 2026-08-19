package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

final class BiomeTransitionTrackerTest {
    @Test
    void establishesABaselineThenShowsAChangeForThreeSeconds() {
        BiomeTransitionTracker tracker = new BiomeTransitionTracker();
        assertTrue(tracker.advance(true, Optional.of("Plains")).isEmpty());

        BiomeTransitionTracker.Notice notice = tracker.advance(
            true,
            Optional.of("Jungle")
        ).orElseThrow();
        assertEquals(new BiomeTransitionTracker.Notice("Plains", "Jungle"), notice);

        for (int tick = 1; tick < BiomeTransitionTracker.DURATION_TICKS; tick++) {
            assertTrue(tracker.advance(true, Optional.empty()).isPresent());
        }
        assertTrue(tracker.advance(true, Optional.empty()).isEmpty());
    }

    @Test
    void aSecondChangeReplacesTheNoticeAndDisabledModeForgetsTheBaseline() {
        BiomeTransitionTracker tracker = new BiomeTransitionTracker();
        tracker.advance(true, Optional.of("Plains"));
        tracker.advance(true, Optional.of("Jungle"));

        assertEquals(
            new BiomeTransitionTracker.Notice("Jungle", "Desert"),
            tracker.advance(true, Optional.of("Desert")).orElseThrow()
        );
        assertTrue(tracker.advance(false, Optional.of("Forest")).isEmpty());
        assertTrue(tracker.advance(true, Optional.of("Taiga")).isEmpty());
    }
}
