package dev.mrfdev.locatorhud;

public final class TargetNameFormatter {
    private TargetNameFormatter() {
    }

    public static String fromRegisteredName(String registeredName) {
        if (registeredName == null || registeredName.isBlank()) {
            return TargetValue.EMPTY;
        }
        return registeredName.trim();
    }
}
