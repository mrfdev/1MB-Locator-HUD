package dev.mrfdev.locatorhud;

import java.util.Objects;

public record TargetDescriptor(String apiAccurate, String friendly) {
    private static final TargetDescriptor EMPTY = new TargetDescriptor(
        TargetValue.EMPTY,
        TargetValue.EMPTY
    );

    public TargetDescriptor {
        Objects.requireNonNull(apiAccurate, "apiAccurate");
        Objects.requireNonNull(friendly, "friendly");
        if (!TargetValue.hasValue(apiAccurate)) {
            apiAccurate = TargetValue.EMPTY;
            friendly = TargetValue.EMPTY;
        } else if (!TargetValue.hasValue(friendly)) {
            friendly = apiAccurate;
        }
    }

    public static TargetDescriptor empty() {
        return EMPTY;
    }

    public static TargetDescriptor apiOnly(String apiAccurate) {
        return new TargetDescriptor(apiAccurate, apiAccurate);
    }

    public boolean hasValue() {
        return TargetValue.hasValue(this.apiAccurate);
    }

    public String display(TargetNameMode mode) {
        Objects.requireNonNull(mode, "mode");
        return switch (mode) {
            case FRIENDLY -> this.friendly;
            case API_ACCURATE -> this.apiAccurate;
        };
    }
}
