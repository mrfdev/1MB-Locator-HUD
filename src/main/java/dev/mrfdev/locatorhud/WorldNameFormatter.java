package dev.mrfdev.locatorhud;

import java.util.Locale;

public final class WorldNameFormatter {
    private WorldNameFormatter() {
    }

    public static String fromIdentifier(String namespace, String path) {
        if ("minecraft".equals(namespace)) {
            return switch (path) {
                case "overworld" -> "Overworld";
                case "the_nether" -> "The Nether";
                case "the_end" -> "The End";
                default -> titleCase(path);
            };
        }
        return titleCase(path) + " (" + namespace + ")";
    }

    static String titleCase(String value) {
        String normalized = value.replace('/', ' ').replace('_', ' ').replace('-', ' ').trim();
        if (normalized.isEmpty()) {
            return "Unknown world";
        }

        StringBuilder output = new StringBuilder(normalized.length());
        boolean capitalize = true;
        for (int index = 0; index < normalized.length(); index++) {
            char character = normalized.charAt(index);
            if (Character.isWhitespace(character)) {
                if (!output.isEmpty() && output.charAt(output.length() - 1) != ' ') {
                    output.append(' ');
                }
                capitalize = true;
            } else if (capitalize) {
                output.append(String.valueOf(character).toUpperCase(Locale.ROOT));
                capitalize = false;
            } else {
                output.append(character);
            }
        }
        return output.toString();
    }
}
