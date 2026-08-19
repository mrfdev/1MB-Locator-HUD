package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.HudPaletteColors;

public enum ColorPalette {
    NONE(
        "None (all white)",
        0x181818,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF
    ),
    DUO_TONE(
        "Duo-tone (gray/white)",
        0x181818,
        0x8E8E8E,
        0xFFFFFF,
        0xC0C0C0,
        0xC0C0C0,
        0xC0C0C0,
        0xC0C0C0,
        0xFFFFFF,
        0xFFFFFF,
        0xFFFFFF,
        0xC0C0C0
    ),
    OCEAN(
        "Ocean",
        0x17283F,
        0x4AA8FF,
        0xF2F7FF,
        0xA9C5DF,
        0xFF7B72,
        0x78D381,
        0x6EC6FF,
        0xFFD166,
        0xA9C5DF,
        0x78D381,
        0xFFD166
    ),
    AMETHYST(
        "Amethyst",
        0x281D38,
        0xC084FC,
        0xFAF5FF,
        0xD8B4FE,
        0xFB7185,
        0x86EFAC,
        0x93C5FD,
        0xFDE68A,
        0xD8B4FE,
        0x86EFAC,
        0xFDE68A
    ),
    EMERALD(
        "Emerald",
        0x142D2B,
        0x34D399,
        0xECFDF5,
        0xA7F3D0,
        0xFDA4AF,
        0x86EFAC,
        0x7DD3FC,
        0xFDE68A,
        0xA7F3D0,
        0x86EFAC,
        0xFDE68A
    ),
    EMBER(
        "Ember",
        0x342119,
        0xFB923C,
        0xFFF7ED,
        0xFED7AA,
        0xFB7185,
        0xA3E635,
        0x67E8F9,
        0xFBBF24,
        0xFED7AA,
        0xA3E635,
        0xFBBF24
    ),
    FROST(
        "Frost",
        0x162A33,
        0x67E8F9,
        0xF0FDFF,
        0xBAE6FD,
        0xFDA4AF,
        0xBEF264,
        0x7DD3FC,
        0xC4B5FD,
        0xBAE6FD,
        0xBEF264,
        0xC4B5FD
    ),
    ROSE(
        "Rose",
        0x331D2C,
        0xF472B6,
        0xFFF1F7,
        0xFBCFE8,
        0xFB7185,
        0x86EFAC,
        0x93C5FD,
        0xFDE68A,
        0xF9A8D4,
        0x86EFAC,
        0xFDE68A
    ),
    GOLD(
        "Gold",
        0x302711,
        0xFBBF24,
        0xFFFBEB,
        0xFDE68A,
        0xFB7185,
        0xA3E635,
        0x67E8F9,
        0xFDA4AF,
        0xFDE68A,
        0xA3E635,
        0xFDA4AF
    );

    private final String displayName;
    private final HudPaletteColors colors;

    ColorPalette(
        String displayName,
        int backgroundRgb,
        int accentRgb,
        int primaryRgb,
        int secondaryRgb,
        int xRgb,
        int yRgb,
        int zRgb,
        int directionRgb,
        int worldRgb,
        int biomeRgb,
        int targetEntityLabelRgb
    ) {
        this.displayName = displayName;
        this.colors = new HudPaletteColors(
            backgroundRgb,
            accentRgb,
            primaryRgb,
            secondaryRgb,
            xRgb,
            yRgb,
            zRgb,
            directionRgb,
            worldRgb,
            biomeRgb,
            targetEntityLabelRgb
        );
    }

    public String displayName() {
        return this.displayName;
    }

    public int backgroundRgb() {
        return this.colors.backgroundRgb();
    }

    public int accent() {
        return this.colors.accent();
    }

    public int primary() {
        return this.colors.primary();
    }

    public int secondary() {
        return this.colors.secondary();
    }

    public int x() {
        return this.colors.x();
    }

    public int y() {
        return this.colors.y();
    }

    public int z() {
        return this.colors.z();
    }

    public int direction() {
        return this.colors.direction();
    }

    public int world() {
        return this.colors.world();
    }

    public int biome() {
        return this.colors.biome();
    }

    public int targetEntityLabel() {
        return this.colors.targetEntityLabel();
    }

    public HudPaletteColors colors() {
        return this.colors;
    }
}
