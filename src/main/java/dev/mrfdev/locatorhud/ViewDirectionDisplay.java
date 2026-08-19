package dev.mrfdev.locatorhud;

public enum ViewDirectionDisplay {
    ON("options.on"),
    WITH_DETAILS("value.locatorhud.view_direction.with_details"),
    OFF("options.off");

    private final String translationKey;

    ViewDirectionDisplay(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public boolean showsDirection() {
        return this != OFF;
    }

    public boolean showsDetails() {
        return this == WITH_DETAILS;
    }

    public static ViewDirectionDisplay fromLegacy(boolean enabled) {
        return enabled ? ON : OFF;
    }
}
