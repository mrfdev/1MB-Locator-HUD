package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class WorldNameFormatterTest {
    @Test
    void givesVanillaDimensionsFriendlyNames() {
        assertEquals("Overworld", WorldNameFormatter.fromIdentifier("minecraft", "overworld"));
        assertEquals("The Nether", WorldNameFormatter.fromIdentifier("minecraft", "the_nether"));
        assertEquals("The End", WorldNameFormatter.fromIdentifier("minecraft", "the_end"));
    }

    @Test
    void makesCustomWorldPathsReadableWithoutDroppingTheirNamespace() {
        assertEquals("Mining World (oneblock)", WorldNameFormatter.fromIdentifier("oneblock", "mining_world"));
        assertEquals("Wild", WorldNameFormatter.fromIdentifier("minecraft", "wild"));
    }
}
