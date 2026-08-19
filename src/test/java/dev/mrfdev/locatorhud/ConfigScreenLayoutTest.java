package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

final class ConfigScreenLayoutTest {
    @Test
    void usesTwoFullWidthColumnsWhenTheScreenCanFitThemAndTheScrollbar() {
        ConfigScreenLayout.Plan plan = ConfigScreenLayout.forScreenWidth(
            ConfigScreenLayout.TWO_COLUMN_THRESHOLD
        );

        assertTrue(plan.twoColumns());
        assertEquals(240, plan.buttonWidth());
        assertEquals(488, plan.contentWidth());
    }

    @Test
    void fallsBackToAWiderSingleColumnBelowTheThreshold() {
        ConfigScreenLayout.Plan nearThreshold = ConfigScreenLayout.forScreenWidth(
            ConfigScreenLayout.TWO_COLUMN_THRESHOLD - 1
        );
        ConfigScreenLayout.Plan normalGui = ConfigScreenLayout.forScreenWidth(320);
        ConfigScreenLayout.Plan narrowGui = ConfigScreenLayout.forScreenWidth(240);

        assertFalse(nearThreshold.twoColumns());
        assertEquals(320, nearThreshold.buttonWidth());
        assertEquals(280, normalGui.buttonWidth());
        assertEquals(200, narrowGui.buttonWidth());
    }

    @Test
    void rejectsNegativeScreenWidths() {
        assertThrows(IllegalArgumentException.class, () -> ConfigScreenLayout.forScreenWidth(-1));
    }

    @Test
    void pairsLongSlidersOnlyWhenTheirCompactLabelsFit() {
        assertFalse(ConfigScreenLayout.pairsPanelSliders(239));
        assertTrue(ConfigScreenLayout.pairsPanelSliders(240));
        assertTrue(ConfigScreenLayout.pairsPanelSliders(320));
    }

    @Test
    void dividesCompactRowsWithoutLosingWidth() {
        assertEquals(List.of(118, 118), ConfigScreenLayout.equalColumnWidths(240, 2, 4));
        assertEquals(List.of(78, 77, 77), ConfigScreenLayout.equalColumnWidths(240, 3, 4));
        assertEquals(List.of(104, 104, 104), ConfigScreenLayout.equalColumnWidths(320, 3, 4));

        assertThrows(
            IllegalArgumentException.class,
            () -> ConfigScreenLayout.equalColumnWidths(2, 2, 1)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> ConfigScreenLayout.equalColumnWidths(10, 0, 1)
        );
    }
}
