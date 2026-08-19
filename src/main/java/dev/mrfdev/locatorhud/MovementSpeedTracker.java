package dev.mrfdev.locatorhud;

public final class MovementSpeedTracker {
    public static final int WINDOW_TICKS = 10;

    private static final double TICKS_PER_SECOND = 20.0D;
    private static final double DISCONTINUITY_DISTANCE_SQUARED = 16.0D * 16.0D;

    private final double[] distances = new double[WINDOW_TICKS];
    private int nextDistance;
    private boolean hasPreviousPosition;
    private double previousX;
    private double previousZ;

    public double update(double x, double z) {
        if (!Double.isFinite(x) || !Double.isFinite(z)) {
            reset();
            return 0.0D;
        }
        if (!this.hasPreviousPosition) {
            remember(x, z);
            return 0.0D;
        }

        double deltaX = x - this.previousX;
        double deltaZ = z - this.previousZ;
        remember(x, z);
        double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
        if (!Double.isFinite(distanceSquared)
            || distanceSquared > DISCONTINUITY_DISTANCE_SQUARED) {
            clearWindow();
            return 0.0D;
        }

        double distance = Math.sqrt(distanceSquared);
        this.distances[this.nextDistance] = distance;
        this.nextDistance = (this.nextDistance + 1) % WINDOW_TICKS;
        return windowDistance() * TICKS_PER_SECOND / WINDOW_TICKS;
    }

    public void reset() {
        clearWindow();
        this.hasPreviousPosition = false;
        this.previousX = 0.0D;
        this.previousZ = 0.0D;
    }

    private void remember(double x, double z) {
        this.previousX = x;
        this.previousZ = z;
        this.hasPreviousPosition = true;
    }

    private void clearWindow() {
        java.util.Arrays.fill(this.distances, 0.0D);
        this.nextDistance = 0;
    }

    private double windowDistance() {
        double sum = 0.0D;
        for (double distance : this.distances) {
            sum += distance;
        }
        return sum;
    }
}
