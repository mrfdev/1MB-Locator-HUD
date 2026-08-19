package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CoordinateCopyFormatTest {
    @Test
    void keepsTheConfigurationOrderAndTranslationKeysStable() {
        assertArrayEquals(
            new CoordinateCopyFormat[] {
                CoordinateCopyFormat.PLAIN,
                CoordinateCopyFormat.VANILLA_TELEPORT,
                CoordinateCopyFormat.CMI_TPPOS
            },
            CoordinateCopyFormat.values()
        );
        assertEquals(
            "value.locatorhud.coordinate_copy_format.plain",
            CoordinateCopyFormat.PLAIN.translationKey()
        );
        assertEquals(
            "value.locatorhud.coordinate_copy_format.vanilla_teleport",
            CoordinateCopyFormat.VANILLA_TELEPORT.translationKey()
        );
        assertEquals(
            "value.locatorhud.coordinate_copy_format.cmi_tppos",
            CoordinateCopyFormat.CMI_TPPOS.translationKey()
        );
    }
}
