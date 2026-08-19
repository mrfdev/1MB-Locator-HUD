package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class WorldNameDisplayTest {
    @Test
    void exposesTheRequestedChoicesInDisplayOrder() {
        assertEquals(3, WorldNameDisplay.values().length);
        assertEquals(
            "value.locatorhud.world_name.in_front",
            WorldNameDisplay.values()[0].translationKey()
        );
        assertEquals(
            "value.locatorhud.world_name.behind",
            WorldNameDisplay.values()[1].translationKey()
        );
        assertEquals("options.off", WorldNameDisplay.values()[2].translationKey());
    }

    @Test
    void identifiesTheRequestedCoordinateSide() {
        assertTrue(WorldNameDisplay.IN_FRONT.showsWorld());
        assertTrue(WorldNameDisplay.IN_FRONT.beforeCoordinates());
        assertFalse(WorldNameDisplay.IN_FRONT.afterCoordinates());

        assertTrue(WorldNameDisplay.BEHIND.showsWorld());
        assertFalse(WorldNameDisplay.BEHIND.beforeCoordinates());
        assertTrue(WorldNameDisplay.BEHIND.afterCoordinates());

        assertFalse(WorldNameDisplay.OFF.showsWorld());
        assertFalse(WorldNameDisplay.OFF.beforeCoordinates());
        assertFalse(WorldNameDisplay.OFF.afterCoordinates());
    }

    @Test
    void migratesTheLegacyBooleanWithoutChangingItsMeaning() {
        assertSame(WorldNameDisplay.BEHIND, WorldNameDisplay.fromLegacy(true));
        assertSame(WorldNameDisplay.OFF, WorldNameDisplay.fromLegacy(false));
    }
}
