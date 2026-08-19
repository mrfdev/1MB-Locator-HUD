package dev.mrfdev.locatorhud;

import java.util.Objects;

public record BiomeThemeSample(String identifier, float temperature, boolean underground) {
    public BiomeThemeSample {
        Objects.requireNonNull(identifier, "identifier");
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        if (!Float.isFinite(temperature)) {
            throw new IllegalArgumentException("temperature must be finite");
        }
    }
}
