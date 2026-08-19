package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.ColorPalette;
import java.util.Objects;

public final class BiomeThemePolicy {
    private static final float COLD_TEMPERATURE = 0.15F;
    private static final float WARM_TEMPERATURE = 0.9F;

    private BiomeThemePolicy() {
    }

    public static ColorPalette paletteFor(BiomeThemeSample sample) {
        Objects.requireNonNull(sample, "sample");
        if (sample.underground()) {
            return ColorPalette.AMETHYST;
        }
        if (sample.temperature() < COLD_TEMPERATURE) {
            return ColorPalette.FROST;
        }
        if (sample.temperature() >= WARM_TEMPERATURE) {
            return ColorPalette.EMBER;
        }
        return ColorPalette.EMERALD;
    }
}
