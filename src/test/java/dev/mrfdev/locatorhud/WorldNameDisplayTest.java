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
        assertEquals("ON (in front)", WorldNameDisplay.values()[0].displayName());
        assertEquals("ON (behind)", WorldNameDisplay.values()[1].displayName());
        assertEquals("OFF", WorldNameDisplay.values()[2].displayName());
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
