package dev.mrfdev.locatorhud;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class CoordinateCopyFormatter {
    private static final Pattern SAFE_COMMAND_TOKEN = Pattern.compile("[A-Za-z0-9_./-]+");
    private static final String PLAYER_PLACEHOLDER = "<playername>";
    private static final String WORLD_PLACEHOLDER = "<world>";

    private CoordinateCopyFormatter() {
    }

    public static Optional<String> format(
        CoordinateCopyFormat format,
        CoordinatePrecision precision,
        double x,
        double y,
        double z,
        String playerName,
        String displayWorld,
        String commandWorld
    ) {
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(precision, "precision");
        Objects.requireNonNull(playerName, "playerName");
        Objects.requireNonNull(displayWorld, "displayWorld");
        Objects.requireNonNull(commandWorld, "commandWorld");
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            return Optional.empty();
        }

        String formattedX = precision.format(x);
        String formattedY = precision.format(y);
        String formattedZ = precision.format(z);
        String formattedWorld = singleLine(displayWorld);
        if (formattedWorld.isEmpty()) {
            formattedWorld = "Unknown";
        }
        return Optional.of(switch (format) {
            case PLAIN -> String.join(
                " ",
                "X", formattedX,
                "Y", formattedY,
                "Z", formattedZ,
                "/", formattedWorld
            );
            case VANILLA_TELEPORT -> String.join(
                " ",
                "/minecraft:teleport", "@s", formattedX, formattedY, formattedZ
            );
            case CMI_TPPOS -> String.join(
                " ",
                "/cmi", "tppos",
                "-p:" + safeCommandToken(playerName, PLAYER_PLACEHOLDER),
                formattedX, formattedY, formattedZ,
                safeCommandToken(commandWorld, WORLD_PLACEHOLDER)
            );
        });
    }

    private static String safeCommandToken(String value, String fallback) {
        return SAFE_COMMAND_TOKEN.matcher(value).matches() ? value : fallback;
    }

    private static String singleLine(String value) {
        StringBuilder result = new StringBuilder(value.length());
        boolean previousWasSpace = false;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            boolean replaceWithSpace = Character.isISOControl(character)
                || Character.isWhitespace(character);
            if (replaceWithSpace) {
                if (!previousWasSpace && !result.isEmpty()) {
                    result.append(' ');
                }
                previousWasSpace = true;
            } else {
                result.append(character);
                previousWasSpace = false;
            }
        }
        return result.toString().strip();
    }
}
