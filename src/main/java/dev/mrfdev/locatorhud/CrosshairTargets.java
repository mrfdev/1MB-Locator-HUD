package dev.mrfdev.locatorhud;

import java.util.Objects;

public record CrosshairTargets(
    TargetDescriptor block,
    TargetDescriptor fluid,
    TargetDescriptor entity
) {
    private static final CrosshairTargets EMPTY = new CrosshairTargets(
        TargetDescriptor.empty(),
        TargetDescriptor.empty(),
        TargetDescriptor.empty()
    );

    public CrosshairTargets(String block, String fluid, String entity) {
        this(
            TargetDescriptor.apiOnly(block),
            TargetDescriptor.apiOnly(fluid),
            TargetDescriptor.apiOnly(entity)
        );
    }

    public CrosshairTargets {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(fluid, "fluid");
        Objects.requireNonNull(entity, "entity");
    }

    public static CrosshairTargets empty() {
        return EMPTY;
    }
}
