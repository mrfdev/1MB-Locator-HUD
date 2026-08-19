package dev.mrfdev.locatorhud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

public final class DiscreteSliderOptions<T> {
    private final List<Entry<T>> entries;

    public DiscreteSliderOptions(List<T> values, ToDoubleFunction<? super T> position) {
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(position, "position");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }

        List<Entry<T>> resolvedEntries = new ArrayList<>(values.size());
        double previousPosition = Double.NEGATIVE_INFINITY;
        for (T value : values) {
            T resolvedValue = Objects.requireNonNull(value, "value");
            double resolvedPosition = position.applyAsDouble(resolvedValue);
            if (!Double.isFinite(resolvedPosition) || resolvedPosition < 0.0 || resolvedPosition > 1.0) {
                throw new IllegalArgumentException("slider positions must be finite and between 0 and 1");
            }
            if (resolvedPosition <= previousPosition) {
                throw new IllegalArgumentException("slider positions must be strictly increasing");
            }
            resolvedEntries.add(new Entry<>(resolvedValue, resolvedPosition));
            previousPosition = resolvedPosition;
        }
        this.entries = List.copyOf(resolvedEntries);
    }

    public List<T> values() {
        return this.entries.stream().map(Entry::value).toList();
    }

    public double position(T value) {
        return entry(value).position();
    }

    public T nearest(double position) {
        if (!Double.isFinite(position)) {
            throw new IllegalArgumentException("position must be finite");
        }
        double clampedPosition = Math.max(0.0, Math.min(1.0, position));
        Entry<T> nearest = this.entries.getFirst();
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Entry<T> candidate : this.entries) {
            double distance = Math.abs(candidate.position() - clampedPosition);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }
        return nearest.value();
    }

    public T step(T value, int direction) {
        int currentIndex = this.entries.indexOf(entry(value));
        int nextIndex = Math.max(0, Math.min(this.entries.size() - 1, currentIndex + Integer.signum(direction)));
        return this.entries.get(nextIndex).value();
    }

    private Entry<T> entry(T value) {
        for (Entry<T> entry : this.entries) {
            if (entry.value().equals(value)) {
                return entry;
            }
        }
        throw new IllegalArgumentException("value is not part of this slider");
    }

    private record Entry<T>(T value, double position) {
    }
}
