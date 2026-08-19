package dev.mrfdev.locatorhud.config;

/**
 * Coalesces repeated configuration changes into one save request after a short tick delay.
 */
public final class ConfigSaveDebouncer {
    private final int delayTicks;
    private int remainingTicks;
    private boolean pending;

    public ConfigSaveDebouncer(int delayTicks) {
        if (delayTicks < 1) {
            throw new IllegalArgumentException("delayTicks must be positive");
        }
        this.delayTicks = delayTicks;
    }

    public void request() {
        this.pending = true;
        this.remainingTicks = this.delayTicks;
    }

    public boolean tick() {
        if (!this.pending) {
            return false;
        }
        this.remainingTicks--;
        if (this.remainingTicks > 0) {
            return false;
        }
        return takePending();
    }

    public boolean takePending() {
        boolean wasPending = this.pending;
        this.pending = false;
        this.remainingTicks = 0;
        return wasPending;
    }

    public void cancel() {
        this.pending = false;
        this.remainingTicks = 0;
    }

    public boolean pending() {
        return this.pending;
    }
}
