package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class TargetNameFormatterTest {
    @Test
    void preservesFullVanillaIdentifiersForApiAccurateDisplay() {
        assertEquals("minecraft:sand", TargetNameFormatter.fromRegisteredName("minecraft:sand"));
        assertEquals("minecraft:water", TargetNameFormatter.fromRegisteredName("minecraft:water"));
        assertEquals("minecraft:oak_boat", TargetNameFormatter.fromRegisteredName("minecraft:oak_boat"));
    }

    @Test
    void retainsCustomNamespacesAndHandlesMissingNames() {
        assertEquals("example:copper_crate", TargetNameFormatter.fromRegisteredName("example:copper_crate"));
        assertEquals("—", TargetNameFormatter.fromRegisteredName(""));
        assertEquals("—", TargetNameFormatter.fromRegisteredName(null));
    }
}
