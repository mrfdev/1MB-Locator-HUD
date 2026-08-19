package dev.mrfdev.locatorhud;

public enum WorldNameDisplay {
    IN_FRONT("ON (in front)"),
    BEHIND("ON (behind)"),
    OFF("OFF");

    private final String displayName;

    WorldNameDisplay(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

    public boolean showsWorld() {
        return this != OFF;
    }

    public boolean beforeCoordinates() {
        return this == IN_FRONT;
    }

    public boolean afterCoordinates() {
        return this == BEHIND;
    }

    public static WorldNameDisplay fromLegacy(boolean enabled) {
        return enabled ? BEHIND : OFF;
    }
}
