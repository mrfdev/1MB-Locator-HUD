package dev.mrfdev.locatorhud;

import java.util.List;

public enum HudScale {
    EXTRA_SMALL("value.locatorhud.hud_scale.extra_small", 60),
    VERY_SMALL("value.locatorhud.hud_scale.very_small", 70),
    COMPACT("value.locatorhud.hud_scale.compact", 80),
    SMALL("value.locatorhud.hud_scale.small", 90),
    NORMAL("value.locatorhud.hud_scale.normal", 100),
    LARGE("value.locatorhud.hud_scale.large", 110),
    EXTRA_LARGE("value.locatorhud.hud_scale.extra_large", 125),
    HUGE("value.locatorhud.hud_scale.huge", 150);

    private static final List<HudScale> STANDARD_CHOICES = List.of(
        EXTRA_SMALL,
        VERY_SMALL,
        COMPACT,
        SMALL,
        NORMAL
    );
    private static final List<HudScale> ACCESSIBILITY_CHOICES = List.of(values());

    private final String translationKey;
    private final int percentage;

    HudScale(String translationKey, int percentage) {
        this.translationKey = translationKey;
        this.percentage = percentage;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public int percentage() {
        return this.percentage;
    }

    public float factor() {
        return this.percentage / 100.0F;
    }

    public boolean accessibilityOnly() {
        return this.percentage > NORMAL.percentage;
    }

    public HudScale standardFallback() {
        return accessibilityOnly() ? NORMAL : this;
    }

    public static List<HudScale> choices(boolean accessibilitySettingsEnabled) {
        return accessibilitySettingsEnabled ? ACCESSIBILITY_CHOICES : STANDARD_CHOICES;
    }
}
