package dev.mrfdev.locatorhud;

import java.util.Objects;
import java.util.Optional;

public final class TargetValueLinger {
    public static final int DURATION_TICKS = 10;

    private final Entry block = new Entry();
    private final Entry fluid = new Entry();
    private final Entry entity = new Entry();
    private long tick;

    public CrosshairTargets advance(
        boolean lingerEnabled,
        Selection selection,
        Optional<CrosshairTargets> sample
    ) {
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(sample, "sample");
        this.tick++;
        CrosshairTargets sampled = sample.orElse(null);
        return new CrosshairTargets(
            this.block.advance(
                selection.block(),
                lingerEnabled,
                sampled == null ? null : sampled.block(),
                this.tick
            ),
            this.fluid.advance(
                selection.fluid(),
                lingerEnabled,
                sampled == null ? null : sampled.fluid(),
                this.tick
            ),
            this.entity.advance(
                selection.entity(),
                lingerEnabled,
                sampled == null ? null : sampled.entity(),
                this.tick
            )
        );
    }

    public void reset() {
        this.tick = 0L;
        this.block.reset();
        this.fluid.reset();
        this.entity.reset();
    }

    public record Selection(boolean block, boolean fluid, boolean entity) {
    }

    private static final class Entry {
        private TargetDescriptor observed = TargetDescriptor.empty();
        private TargetDescriptor held = TargetDescriptor.empty();
        private long expiresAtTick;

        private TargetDescriptor advance(
            boolean requested,
            boolean lingerEnabled,
            TargetDescriptor sample,
            long tick
        ) {
            if (!requested) {
                reset();
                return TargetDescriptor.empty();
            }

            if (sample != null) {
                boolean previouslyPresent = this.observed.hasValue();
                if (sample.hasValue()) {
                    this.observed = sample;
                    clearHold();
                } else {
                    if (previouslyPresent && lingerEnabled) {
                        this.held = this.observed;
                        this.expiresAtTick = tick + DURATION_TICKS;
                    }
                    this.observed = TargetDescriptor.empty();
                }
            }

            if (this.observed.hasValue()) {
                return this.observed;
            }
            if (lingerEnabled
                && this.held.hasValue()
                && tick < this.expiresAtTick) {
                return this.held;
            }
            clearHold();
            return TargetDescriptor.empty();
        }

        private void reset() {
            this.observed = TargetDescriptor.empty();
            clearHold();
        }

        private void clearHold() {
            this.held = TargetDescriptor.empty();
            this.expiresAtTick = 0L;
        }
    }
}
