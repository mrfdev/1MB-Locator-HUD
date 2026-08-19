package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import dev.mrfdev.locatorhud.config.ColorPalette;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class BiomeThemeControllerTest {
    @Test
    void delaysBiomeChangesThenBlendsAndRestoresTheManualThemeImmediately() {
        BiomeThemeController controller = new BiomeThemeController();
        Optional<BiomeThemeSample> cold = Optional.of(
            new BiomeThemeSample("minecraft:snowy_plains", 0.0F, false)
        );

        for (int tick = 1; tick < BiomeThemeController.SWITCH_DELAY_TICKS; tick++) {
            assertEquals(
                ColorPalette.OCEAN.colors(),
                controller.advance(true, ColorPalette.OCEAN, cold)
            );
        }

        HudPaletteColors firstBlend = controller.advance(true, ColorPalette.OCEAN, cold);
        assertNotEquals(ColorPalette.OCEAN.colors(), firstBlend);
        assertNotEquals(ColorPalette.FROST.colors(), firstBlend);

        HudPaletteColors settled = firstBlend;
        for (int tick = 1; tick < BiomeThemeController.TRANSITION_TICKS; tick++) {
            settled = controller.advance(true, ColorPalette.OCEAN, cold);
        }
        assertEquals(ColorPalette.FROST.colors(), settled);

        assertEquals(
            ColorPalette.GOLD.colors(),
            controller.advance(false, ColorPalette.GOLD, cold)
        );
    }
}
