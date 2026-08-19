package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class TargetDescriptorTest {
    @Test
    void selectsFriendlyOrStableNamesWithoutLosingEitherRepresentation() {
        TargetDescriptor target = new TargetDescriptor("minecraft:oak_log", "Oak Log");

        assertTrue(target.hasValue());
        assertEquals("Oak Log", target.display(TargetNameMode.FRIENDLY));
        assertEquals("minecraft:oak_log", target.display(TargetNameMode.API_ACCURATE));
        assertEquals(
            "value.locatorhud.target_name_mode.friendly",
            TargetNameMode.FRIENDLY.translationKey()
        );
        assertEquals(
            "value.locatorhud.target_name_mode.api_accurate",
            TargetNameMode.API_ACCURATE.translationKey()
        );
    }

    @Test
    void fallsBackToTheApiNameWhenFriendlyTextIsUnavailable() {
        TargetDescriptor target = new TargetDescriptor("example:copper_crate", " ");

        assertEquals("example:copper_crate", target.friendly());
    }

    @Test
    void canonicalizesMissingTargets() {
        TargetDescriptor target = new TargetDescriptor("", "Misleading name");

        assertFalse(target.hasValue());
        assertEquals(TargetValue.EMPTY, target.apiAccurate());
        assertEquals(TargetValue.EMPTY, target.friendly());
        assertSame(TargetDescriptor.empty(), TargetDescriptor.empty());
    }
}
