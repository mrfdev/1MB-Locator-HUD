package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CoordinateCopyFormatterTest {
    @Test
    void formatsPlainCoordinatesWithTheVisibleWorldName() {
        assertEquals(
            "X 12.3 Y 64.0 Z -8.6 / General Survival",
            format(
                CoordinateCopyFormat.PLAIN,
                CoordinatePrecision.ONE_DECIMAL,
                "mrfloris",
                "General\nSurvival",
                "general"
            )
        );
    }

    @Test
    void formatsTheNamespacedVanillaSelfTeleportCommand() {
        assertEquals(
            "/minecraft:teleport @s 12.35 64.00 -8.57",
            format(
                CoordinateCopyFormat.VANILLA_TELEPORT,
                CoordinatePrecision.TWO_DECIMALS,
                "mrfloris",
                "General",
                "general"
            )
        );
    }

    @Test
    void formatsTheDocumentedCmiTpposArgumentOrder() {
        assertEquals(
            "/cmi tppos -p:mrfloris 12 64 -9 general",
            format(
                CoordinateCopyFormat.CMI_TPPOS,
                CoordinatePrecision.NONE,
                "mrfloris",
                "General",
                "general"
            )
        );
    }

    @Test
    void usesVisiblePlaceholdersInsteadOfUnsafeCommandTokens() {
        assertEquals(
            "/cmi tppos -p:<playername> 12 64 -9 <world>",
            format(
                CoordinateCopyFormat.CMI_TPPOS,
                CoordinatePrecision.NONE,
                "name with spaces",
                "General",
                "general;op"
            )
        );
    }

    @Test
    void rejectsNonFiniteCoordinates() {
        assertTrue(CoordinateCopyFormatter.format(
            CoordinateCopyFormat.PLAIN,
            CoordinatePrecision.ONE_DECIMAL,
            Double.NaN,
            64.0D,
            0.0D,
            "mrfloris",
            "General",
            "general"
        ).isEmpty());
    }

    private static String format(
        CoordinateCopyFormat format,
        CoordinatePrecision precision,
        String playerName,
        String displayWorld,
        String commandWorld
    ) {
        return CoordinateCopyFormatter.format(
            format,
            precision,
            12.345D,
            64.0D,
            -8.567D,
            playerName,
            displayWorld,
            commandWorld
        ).orElseThrow();
    }
}
