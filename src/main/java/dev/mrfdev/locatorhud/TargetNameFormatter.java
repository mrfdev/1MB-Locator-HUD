package dev.mrfdev.locatorhud;

public final class TargetNameFormatter {
    private static final String VANILLA_NAMESPACE = "minecraft:";

    private TargetNameFormatter() {
    }

    public static String fromRegisteredName(String registeredName) {
        if (registeredName == null || registeredName.isBlank()) {
            return TargetValue.EMPTY;
        }
        if (registeredName.startsWith(VANILLA_NAMESPACE)) {
            return registeredName.substring(VANILLA_NAMESPACE.length());
        }
        return registeredName;
    }
}
