package dev.mrfdev.locatorhud;

public final class TargetValue {
    public static final String EMPTY = "—";

    private TargetValue() {
    }

    public static boolean hasValue(String value) {
        return value != null && !value.isBlank() && !EMPTY.equals(value);
    }
}
