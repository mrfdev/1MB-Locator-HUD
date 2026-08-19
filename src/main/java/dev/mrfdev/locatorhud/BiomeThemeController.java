package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.ColorPalette;
import java.util.Objects;
import java.util.Optional;

public final class BiomeThemeController {
    public static final int SWITCH_DELAY_TICKS = 10;
    public static final int TRANSITION_TICKS = 20;

    private ColorPalette activePalette;
    private ColorPalette pendingPalette;
    private int pendingTicks;
    private HudPaletteColors currentColors;
    private HudPaletteColors transitionStart;
    private HudPaletteColors transitionTarget;
    private int transitionTicks;

    public HudPaletteColors advance(
        boolean enabled,
        ColorPalette manualPalette,
        Optional<BiomeThemeSample> sample
    ) {
        Objects.requireNonNull(manualPalette, "manualPalette");
        Objects.requireNonNull(sample, "sample");
        if (!enabled) {
            reset();
            return manualPalette.colors();
        }

        initialize(manualPalette);
        ColorPalette candidate = sample
            .map(BiomeThemePolicy::paletteFor)
            .orElse(manualPalette);
        observeCandidate(candidate);
        advanceTransition();
        return this.currentColors;
    }

    public void reset() {
        this.activePalette = null;
        this.pendingPalette = null;
        this.pendingTicks = 0;
        this.currentColors = null;
        this.transitionStart = null;
        this.transitionTarget = null;
        this.transitionTicks = 0;
    }

    private void initialize(ColorPalette manualPalette) {
        if (this.currentColors != null) {
            return;
        }
        this.activePalette = manualPalette;
        this.currentColors = manualPalette.colors();
        this.transitionStart = this.currentColors;
        this.transitionTarget = this.currentColors;
        this.transitionTicks = TRANSITION_TICKS;
    }

    private void observeCandidate(ColorPalette candidate) {
        if (candidate == this.activePalette) {
            this.pendingPalette = null;
            this.pendingTicks = 0;
            return;
        }
        if (candidate != this.pendingPalette) {
            this.pendingPalette = candidate;
            this.pendingTicks = 1;
            return;
        }

        this.pendingTicks++;
        if (this.pendingTicks >= SWITCH_DELAY_TICKS) {
            startTransition(candidate);
        }
    }

    private void startTransition(ColorPalette palette) {
        this.activePalette = palette;
        this.pendingPalette = null;
        this.pendingTicks = 0;
        this.transitionStart = this.currentColors;
        this.transitionTarget = palette.colors();
        this.transitionTicks = 0;
    }

    private void advanceTransition() {
        if (this.transitionTicks >= TRANSITION_TICKS) {
            return;
        }
        this.transitionTicks++;
        this.currentColors = HudPaletteColors.interpolate(
            this.transitionStart,
            this.transitionTarget,
            (double) this.transitionTicks / TRANSITION_TICKS
        );
    }
}
