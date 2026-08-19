package dev.mrfdev.locatorhud;

public enum ViewDirectionDisplay {
    ON,
    WITH_DETAILS,
    OFF;

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
