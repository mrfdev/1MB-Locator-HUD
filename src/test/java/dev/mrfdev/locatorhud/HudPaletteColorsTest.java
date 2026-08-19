package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class HudPaletteColorsTest {
    @Test
    void interpolatesEveryColorRoleAndClampsTransitionProgress() {
        HudPaletteColors dark = palette(0x000000);
        HudPaletteColors light = new HudPaletteColors(
            0x020202,
            0x040404,
            0x060606,
            0x080808,
            0x0A0A0A,
            0x0C0C0C,
            0x0E0E0E,
            0x101010,
            0x121212,
            0x141414,
            0x161616
        );
        HudPaletteColors midpoint = new HudPaletteColors(
            0x010101,
            0x020202,
            0x030303,
            0x040404,
            0x050505,
            0x060606,
            0x070707,
            0x080808,
            0x090909,
            0x0A0A0A,
            0x0B0B0B
        );

        assertEquals(dark, HudPaletteColors.interpolate(dark, light, -1.0D));
        assertEquals(midpoint, HudPaletteColors.interpolate(dark, light, 0.5D));
        assertEquals(light, HudPaletteColors.interpolate(dark, light, 2.0D));
    }

    private static HudPaletteColors palette(int rgb) {
        return new HudPaletteColors(
            rgb,
            rgb,
            rgb,
            rgb,
            rgb,
            rgb,
            rgb,
            rgb,
            rgb,
            rgb,
            rgb
        );
    }
}
