package dev.mrfdev.locatorhud;

import java.util.Objects;
import java.util.Optional;

public final class OverworldNetherLens {
    private static final String VANILLA_NAMESPACE = "minecraft";
    private static final double COORDINATE_SCALE = 8.0D;

    private OverworldNetherLens() {
    }

    public static SourceDimension classify(String namespace, String path) {
        Objects.requireNonNull(namespace, "namespace");
        Objects.requireNonNull(path, "path");
        if (!VANILLA_NAMESPACE.equals(namespace)) {
            return SourceDimension.UNSUPPORTED;
        }
        return switch (path) {
            case "overworld" -> SourceDimension.OVERWORLD;
            case "the_nether" -> SourceDimension.NETHER;
            default -> SourceDimension.UNSUPPORTED;
        };
    }

    public static Optional<Projection> project(SourceDimension source, double x, double z) {
        Objects.requireNonNull(source, "source");
        if (!Double.isFinite(x) || !Double.isFinite(z)) {
            return Optional.empty();
        }
        return switch (source) {
            case OVERWORLD -> projection(
                Destination.NETHER,
                x / COORDINATE_SCALE,
                z / COORDINATE_SCALE
            );
            case NETHER -> projection(
                Destination.OVERWORLD,
                x * COORDINATE_SCALE,
                z * COORDINATE_SCALE
            );
            case UNSUPPORTED -> Optional.empty();
        };
    }

    private static Optional<Projection> projection(
        Destination destination,
        double projectedX,
        double projectedZ
    ) {
        if (!Double.isFinite(projectedX) || !Double.isFinite(projectedZ)) {
            return Optional.empty();
        }
        return Optional.of(new Projection(destination, projectedX, projectedZ));
    }

    public enum SourceDimension {
        OVERWORLD,
        NETHER,
        UNSUPPORTED
    }

    public enum Destination {
        OVERWORLD("hud.locatorhud.coordinate_lens.overworld"),
        NETHER("hud.locatorhud.coordinate_lens.nether");

        private final String translationKey;

        Destination(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return this.translationKey;
        }
    }

    public record Projection(Destination destination, double x, double z) {
        public Projection {
            Objects.requireNonNull(destination, "destination");
            if (!Double.isFinite(x) || !Double.isFinite(z)) {
                throw new IllegalArgumentException("projected coordinates must be finite");
            }
        }
    }
}
