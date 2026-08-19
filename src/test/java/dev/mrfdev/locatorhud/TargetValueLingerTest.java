package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

final class TargetValueLingerTest {
    private static final TargetValueLinger.Selection BLOCK_ONLY =
        new TargetValueLinger.Selection(true, false, false);

    @Test
    void keepsTheLastTargetForExactlyHalfASecondWhenEnabled() {
        TargetValueLinger linger = new TargetValueLinger();
        assertEquals("stone", linger.advance(
            true,
            BLOCK_ONLY,
            Optional.of(new CrosshairTargets(
                new TargetDescriptor("stone", "Stone"),
                TargetDescriptor.empty(),
                TargetDescriptor.empty()
            ))
        ).block().apiAccurate());
        CrosshairTargets held = linger.advance(
            true,
            BLOCK_ONLY,
            Optional.of(CrosshairTargets.empty())
        );
        assertEquals("stone", held.block().apiAccurate());
        assertEquals("Stone", held.block().friendly());

        for (int tick = 1; tick < TargetValueLinger.DURATION_TICKS; tick++) {
            assertEquals(
                "stone",
                linger.advance(true, BLOCK_ONLY, Optional.empty()).block().apiAccurate()
            );
        }
        assertEquals(
            TargetValue.EMPTY,
            linger.advance(true, BLOCK_ONLY, Optional.empty()).block().apiAccurate()
        );
    }

    @Test
    void immediateModeDoesNotRetainEmptyTargets() {
        TargetValueLinger linger = new TargetValueLinger();
        linger.advance(
            false,
            BLOCK_ONLY,
            Optional.of(new CrosshairTargets("stone", TargetValue.EMPTY, TargetValue.EMPTY))
        );

        assertEquals(TargetValue.EMPTY, linger.advance(
            false,
            BLOCK_ONLY,
            Optional.of(CrosshairTargets.empty())
        ).block().apiAccurate());
    }

    @Test
    void targetTypesLingerIndependentlyAndDisabledTypesAreCleared() {
        TargetValueLinger linger = new TargetValueLinger();
        TargetValueLinger.Selection all = new TargetValueLinger.Selection(true, true, true);
        linger.advance(
            true,
            all,
            Optional.of(new CrosshairTargets("stone", "water", "cow"))
        );

        CrosshairTargets partial = linger.advance(
            true,
            new TargetValueLinger.Selection(false, true, true),
            Optional.of(new CrosshairTargets(TargetValue.EMPTY, TargetValue.EMPTY, "pig"))
        );

        assertEquals(TargetValue.EMPTY, partial.block().apiAccurate());
        assertEquals("water", partial.fluid().apiAccurate());
        assertEquals("pig", partial.entity().apiAccurate());
    }
}
