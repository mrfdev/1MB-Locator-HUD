package dev.mrfdev.locatorhud;

public enum TargetNameMode {
    FRIENDLY("value.locatorhud.target_name_mode.friendly"),
    API_ACCURATE("value.locatorhud.target_name_mode.api_accurate");

    private final String translationKey;

    TargetNameMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return this.translationKey;
    }
}
