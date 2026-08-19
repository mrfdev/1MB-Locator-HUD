package dev.mrfdev.locatorhud;

public enum CoordinateCopyFormat {
    PLAIN("value.locatorhud.coordinate_copy_format.plain"),
    VANILLA_TELEPORT("value.locatorhud.coordinate_copy_format.vanilla_teleport"),
    CMI_TPPOS("value.locatorhud.coordinate_copy_format.cmi_tppos");

    private final String translationKey;

    CoordinateCopyFormat(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return this.translationKey;
    }
}
