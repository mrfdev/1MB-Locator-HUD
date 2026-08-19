package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class ViewDirectionDisplayTest {
    @Test
    void keepsTheUiOrderAndVisibilitySemanticsExplicit() {
        assertArrayEquals(
            new ViewDirectionDisplay[] {
                ViewDirectionDisplay.ON,
                ViewDirectionDisplay.WITH_DETAILS,
                ViewDirectionDisplay.OFF
            },
            ViewDirectionDisplay.values()
        );
        assertTrue(ViewDirectionDisplay.ON.showsDirection());
        assertFalse(ViewDirectionDisplay.ON.showsDetails());
        assertTrue(ViewDirectionDisplay.WITH_DETAILS.showsDirection());
        assertTrue(ViewDirectionDisplay.WITH_DETAILS.showsDetails());
        assertFalse(ViewDirectionDisplay.OFF.showsDirection());
        assertFalse(ViewDirectionDisplay.OFF.showsDetails());
    }

    @Test
    void mapsTheLegacyBooleanWithoutEnablingDetails() {
        assertSame(ViewDirectionDisplay.ON, ViewDirectionDisplay.fromLegacy(true));
        assertSame(ViewDirectionDisplay.OFF, ViewDirectionDisplay.fromLegacy(false));
    }
}
