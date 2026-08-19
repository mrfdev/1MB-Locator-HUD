package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.mrfdev.locatorhud.config.BackgroundOpacity;
import java.util.List;
import org.junit.jupiter.api.Test;

final class DiscreteSliderOptionsTest {
    @Test
    void preservesRealPositionsForOpacityAndEvenlySpreadsStandardScaleChoices() {
        DiscreteSliderOptions<BackgroundOpacity> opacity = new DiscreteSliderOptions<>(
            List.of(BackgroundOpacity.values()),
            BackgroundOpacity::sliderPosition
        );
        DiscreteSliderOptions<HudScale> scale = DiscreteSliderOptions.evenlySpaced(
            HudScale.choices(false)
        );

        assertEquals(0.24D, opacity.position(BackgroundOpacity.LIGHT));
        assertEquals(0.5D, scale.position(HudScale.COMPACT));
        assertEquals(HudScale.choices(false), scale.values());
        assertEquals(1.0D, scale.position(HudScale.NORMAL));
    }

    @Test
    void evenlySpreadsAccessibilityChoicesAcrossTheFullTrack() {
        DiscreteSliderOptions<HudScale> options = DiscreteSliderOptions.evenlySpaced(
            HudScale.choices(true)
        );

        assertEquals(0.0D, options.position(HudScale.EXTRA_SMALL));
        assertEquals(4.0D / 7.0D, options.position(HudScale.NORMAL));
        assertEquals(5.0D / 7.0D, options.position(HudScale.LARGE));
        assertEquals(1.0D, options.position(HudScale.HUGE));
    }

    @Test
    void snapsToTheNearestStopAndKeepsTheEarlierChoiceOnTies() {
        DiscreteSliderOptions<HudScale> options = DiscreteSliderOptions.evenlySpaced(
            HudScale.choices(false)
        );

        assertSame(HudScale.EXTRA_SMALL, options.nearest(-1.0));
        assertSame(HudScale.NORMAL, options.nearest(2.0));
        assertSame(HudScale.COMPACT, options.nearest(0.4));
        assertSame(HudScale.EXTRA_SMALL, options.nearest(0.125));
    }

    @Test
    void keyboardStepsToAdjacentChoicesAndClampsAtEndpoints() {
        DiscreteSliderOptions<HudScale> options = DiscreteSliderOptions.evenlySpaced(
            HudScale.choices(false)
        );

        assertSame(HudScale.VERY_SMALL, options.step(HudScale.EXTRA_SMALL, 1));
        assertSame(HudScale.EXTRA_SMALL, options.step(HudScale.EXTRA_SMALL, -1));
        assertSame(HudScale.SMALL, options.step(HudScale.NORMAL, -1));
        assertSame(HudScale.NORMAL, options.step(HudScale.NORMAL, 1));
    }

    @Test
    void rejectsInvalidOptionSetsAndInputs() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new DiscreteSliderOptions<String>(List.of(), value -> 0.0)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> DiscreteSliderOptions.evenlySpaced(List.of())
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new DiscreteSliderOptions<>(List.of("a", "b"), value -> 0.5)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new DiscreteSliderOptions<>(List.of("a"), value -> Double.NaN)
        );

        DiscreteSliderOptions<String> options = new DiscreteSliderOptions<>(List.of("a"), value -> 0.0);
        assertEquals(0.0D, DiscreteSliderOptions.evenlySpaced(List.of("a")).position("a"));
        assertThrows(IllegalArgumentException.class, () -> options.nearest(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> options.position("missing"));
    }
}
