package dev.mrfdev.locatorhud;

import java.util.Objects;
import java.util.Optional;

public final class BiomeTransitionTracker {
    public static final int DURATION_TICKS = 60;

    private long tick;
    private String currentBiome;
    private Notice notice;
    private long noticeExpiresAtTick;

    public Optional<Notice> advance(boolean enabled, Optional<String> observedBiome) {
        Objects.requireNonNull(observedBiome, "observedBiome");
        if (!enabled) {
            reset();
            return Optional.empty();
        }

        this.tick++;
        observedBiome.filter(BiomeTransitionTracker::isUsable).ifPresent(this::observe);
        if (this.notice != null && this.tick >= this.noticeExpiresAtTick) {
            this.notice = null;
            this.noticeExpiresAtTick = 0L;
        }
        return Optional.ofNullable(this.notice);
    }

    public void reset() {
        this.tick = 0L;
        this.currentBiome = null;
        this.notice = null;
        this.noticeExpiresAtTick = 0L;
    }

    private void observe(String biome) {
        if (this.currentBiome == null) {
            this.currentBiome = biome;
            return;
        }
        if (this.currentBiome.equals(biome)) {
            return;
        }
        this.notice = new Notice(this.currentBiome, biome);
        this.noticeExpiresAtTick = this.tick + DURATION_TICKS;
        this.currentBiome = biome;
    }

    private static boolean isUsable(String biome) {
        return biome != null && !biome.isBlank();
    }

    public record Notice(String from, String to) {
        public Notice {
            Objects.requireNonNull(from, "from");
            Objects.requireNonNull(to, "to");
            if (from.isBlank() || to.isBlank() || from.equals(to)) {
                throw new IllegalArgumentException("biome transition must contain two distinct names");
            }
        }
    }
}
