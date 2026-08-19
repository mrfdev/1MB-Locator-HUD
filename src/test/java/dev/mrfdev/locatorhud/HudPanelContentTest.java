package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

final class HudPanelContentTest {
    @Test
    void defensivelyCopiesRowsAndParts() {
        List<HudRowPart> mutableParts = new ArrayList<>(List.of(
            HudText.of("value", HudTextRole.PRIMARY)
        ));
        HudRow row = new HudRow(mutableParts);
        List<HudRow> mutableRows = new ArrayList<>(List.of(row));
        HudPanelContent content = new HudPanelContent(mutableRows);

        mutableParts.clear();
        mutableRows.clear();

        assertThrows(UnsupportedOperationException.class, () -> row.parts().clear());
        assertThrows(UnsupportedOperationException.class, () -> content.rows().clear());
    }

    @Test
    void rejectsNegativePixelGaps() {
        assertThrows(IllegalArgumentException.class, () -> new HudGap(-1));
    }
}
