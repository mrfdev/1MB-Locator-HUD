package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertSame;

import dev.mrfdev.locatorhud.config.ColorPalette;
import org.junit.jupiter.api.Test;

final class BiomeThemePolicyTest {
    @Test
    void mapsLocalBiomeClimateToAStableExistingTheme() {
        assertSame(
            ColorPalette.AMETHYST,
            BiomeThemePolicy.paletteFor(new BiomeThemeSample("minecraft:lush_caves", 0.5F, true))
        );
        assertSame(
            ColorPalette.FROST,
            BiomeThemePolicy.paletteFor(new BiomeThemeSample("minecraft:snowy_plains", 0.0F, false))
        );
        assertSame(
            ColorPalette.EMBER,
            BiomeThemePolicy.paletteFor(new BiomeThemeSample("minecraft:desert", 2.0F, false))
        );
        assertSame(
            ColorPalette.EMERALD,
            BiomeThemePolicy.paletteFor(new BiomeThemeSample("minecraft:plains", 0.8F, false))
        );
    }
}
