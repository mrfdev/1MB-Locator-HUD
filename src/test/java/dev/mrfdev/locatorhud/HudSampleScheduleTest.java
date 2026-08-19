package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class HudSampleScheduleTest {
    @Test
    void samplesRequestedDataImmediatelyThenUsesIndependentCadences() {
        HudSampleSchedule schedule = new HudSampleSchedule();

        HudSampleSchedule.Refresh first = schedule.advance(true, 10, 64, 20, 1);
        HudSampleSchedule.Refresh second = schedule.advance(true, 10, 64, 20, 1);
        HudSampleSchedule.Refresh third = schedule.advance(true, 10, 64, 20, 1);

        assertTrue(first.biome());
        assertTrue(first.targets());
        assertFalse(second.biome());
        assertFalse(second.targets());
        assertFalse(third.biome());
        assertTrue(third.targets());
    }

    @Test
    void refreshesBiomeImmediatelyWhenThePlayerChangesBlockPosition() {
        HudSampleSchedule schedule = new HudSampleSchedule();
        schedule.advance(true, 10, 64, 20, 0);

        HudSampleSchedule.Refresh sameBlock = schedule.advance(true, 10, 64, 20, 0);
        HudSampleSchedule.Refresh differentBlock = schedule.advance(true, 11, 64, 20, 0);

        assertFalse(sameBlock.biome());
        assertTrue(differentBlock.biome());
    }

    @Test
    void refreshesBiomeAtLeastOncePerSecondWhileItIsRequested() {
        HudSampleSchedule schedule = new HudSampleSchedule();
        schedule.advance(true, 10, 64, 20, 0);

        for (int tick = 2; tick < 21; tick++) {
            assertFalse(schedule.advance(true, 10, 64, 20, 0).biome());
        }

        assertTrue(schedule.advance(true, 10, 64, 20, 0).biome());
    }

    @Test
    void refreshesTargetsWhenTheRequestedTargetSetChangesOrIsReenabled() {
        HudSampleSchedule schedule = new HudSampleSchedule();
        schedule.advance(false, 0, 0, 0, 1);

        assertTrue(schedule.advance(false, 0, 0, 0, 3).targets());
        assertFalse(schedule.advance(false, 0, 0, 0, 0).targets());
        assertTrue(schedule.advance(false, 0, 0, 0, 3).targets());
    }

    @Test
    void resetMakesTheNextRequestedSamplesImmediate() {
        HudSampleSchedule schedule = new HudSampleSchedule();
        schedule.advance(true, 10, 64, 20, 1);
        schedule.reset();

        HudSampleSchedule.Refresh refresh = schedule.advance(true, 10, 64, 20, 1);

        assertTrue(refresh.biome());
        assertTrue(refresh.targets());
    }
}
