package dev.mrfdev.locatorhud;

import java.util.Objects;

public record HudPaletteColors(
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
    public HudPaletteColors {
        validateRgb(backgroundRgb, "backgroundRgb");
        validateRgb(accentRgb, "accentRgb");
        validateRgb(primaryRgb, "primaryRgb");
        validateRgb(secondaryRgb, "secondaryRgb");
        validateRgb(xRgb, "xRgb");
        validateRgb(yRgb, "yRgb");
        validateRgb(zRgb, "zRgb");
        validateRgb(directionRgb, "directionRgb");
        validateRgb(worldRgb, "worldRgb");
        validateRgb(biomeRgb, "biomeRgb");
        validateRgb(targetEntityLabelRgb, "targetEntityLabelRgb");
    }

    public static HudPaletteColors interpolate(
        HudPaletteColors start,
        HudPaletteColors end,
        double progress
    ) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (!Double.isFinite(progress)) {
            throw new IllegalArgumentException("progress must be finite");
        }
        if (progress <= 0.0D) {
            return start;
        }
        if (progress >= 1.0D) {
            return end;
        }
        return new HudPaletteColors(
            interpolateRgb(start.backgroundRgb, end.backgroundRgb, progress),
            interpolateRgb(start.accentRgb, end.accentRgb, progress),
            interpolateRgb(start.primaryRgb, end.primaryRgb, progress),
            interpolateRgb(start.secondaryRgb, end.secondaryRgb, progress),
            interpolateRgb(start.xRgb, end.xRgb, progress),
            interpolateRgb(start.yRgb, end.yRgb, progress),
            interpolateRgb(start.zRgb, end.zRgb, progress),
            interpolateRgb(start.directionRgb, end.directionRgb, progress),
            interpolateRgb(start.worldRgb, end.worldRgb, progress),
            interpolateRgb(start.biomeRgb, end.biomeRgb, progress),
            interpolateRgb(
                start.targetEntityLabelRgb,
                end.targetEntityLabelRgb,
                progress
            )
        );
    }

    public int accent() {
        return opaque(this.accentRgb);
    }

    public int primary() {
        return opaque(this.primaryRgb);
    }

    public int secondary() {
        return opaque(this.secondaryRgb);
    }

    public int x() {
        return opaque(this.xRgb);
    }

    public int y() {
        return opaque(this.yRgb);
    }

    public int z() {
        return opaque(this.zRgb);
    }

    public int direction() {
        return opaque(this.directionRgb);
    }

    public int world() {
        return opaque(this.worldRgb);
    }

    public int biome() {
        return opaque(this.biomeRgb);
    }

    public int targetEntityLabel() {
        return opaque(this.targetEntityLabelRgb);
    }

    private static int interpolateRgb(int start, int end, double progress) {
        int red = interpolateChannel(start >> 16 & 0xFF, end >> 16 & 0xFF, progress);
        int green = interpolateChannel(start >> 8 & 0xFF, end >> 8 & 0xFF, progress);
        int blue = interpolateChannel(start & 0xFF, end & 0xFF, progress);
        return red << 16 | green << 8 | blue;
    }

    private static int interpolateChannel(int start, int end, double progress) {
        return (int) Math.round(start + (end - start) * progress);
    }

    private static int opaque(int rgb) {
        return 0xFF000000 | rgb;
    }

    private static void validateRgb(int rgb, String name) {
        if (rgb < 0 || rgb > 0xFFFFFF) {
            throw new IllegalArgumentException(name + " must be a 24-bit RGB value");
        }
    }
}
