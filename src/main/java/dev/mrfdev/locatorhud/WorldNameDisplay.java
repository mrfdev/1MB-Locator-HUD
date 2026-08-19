package dev.mrfdev.locatorhud;

public enum WorldNameDisplay {
    IN_FRONT("value.locatorhud.world_name.in_front"),
    BEHIND("value.locatorhud.world_name.behind"),
    OFF("options.off");

    private final String translationKey;

    WorldNameDisplay(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return this.translationKey;
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
