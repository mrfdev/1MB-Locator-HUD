package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class MainPanelContentTest {
    private static final MainPanelContent.Values VALUES = new MainPanelContent.Values(
        "12.3",
        "64.0",
        "-8.5",
        "12",
        "64",
        "-9",
        "Overworld",
        "North",
        "143°",
        "52°↑"
    );

    @Test
    void composesCoordinatesWorldAndViewInDisplayOrder() {
        HudPanelContent content = MainPanelContent.compose(
            CoordinateDisplayMode.BOTH,
            WorldNameDisplay.IN_FRONT,
            true,
            true,
            VALUES,
            HudLayout.forPanel(true)
        );

        assertEquals(List.of(
            row(
                truncatable("Overworld", HudTextRole.WORLD),
                text(" / ", HudTextRole.ACCENT),
                text("X ", HudTextRole.X), text("12.3", HudTextRole.PRIMARY),
                gap(9),
                text("Y ", HudTextRole.Y), text("64.0", HudTextRole.PRIMARY),
                gap(9),
                text("Z ", HudTextRole.Z), text("-8.5", HudTextRole.PRIMARY)
            ),
            row(
                text("BLOCK: ", HudTextRole.SECONDARY),
                text("X ", HudTextRole.X), text("12", HudTextRole.PRIMARY),
                gap(9),
                text("Y ", HudTextRole.Y), text("64", HudTextRole.PRIMARY),
                gap(9),
                text("Z ", HudTextRole.Z), text("-9", HudTextRole.PRIMARY)
            ),
            row(
                text("North", HudTextRole.DIRECTION),
                text(" (", HudTextRole.SECONDARY),
                text("143°", HudTextRole.PRIMARY),
                text(" • ", HudTextRole.ACCENT),
                text("52°↑", HudTextRole.PRIMARY),
                text(")", HudTextRole.SECONDARY)
            )
        ), content.rows());
    }

    @Test
    void usesAStandaloneWorldRowWhenCoordinatesAreHidden() {
        HudPanelContent content = MainPanelContent.compose(
            CoordinateDisplayMode.HIDDEN,
            WorldNameDisplay.BEHIND,
            false,
            false,
            VALUES,
            HudLayout.forPanel(true)
        );

        assertEquals(List.of(row(truncatable("Overworld", HudTextRole.WORLD))), content.rows());
    }

    @Test
    void omitsParenthesesWhenOnlyAnglesAreVisible() {
        HudPanelContent content = MainPanelContent.compose(
            CoordinateDisplayMode.HIDDEN,
            WorldNameDisplay.OFF,
            false,
            true,
            VALUES,
            HudLayout.forPanel(true)
        );

        assertEquals(List.of(row(
            text("143°", HudTextRole.PRIMARY),
            text(" • ", HudTextRole.ACCENT),
            text("52°↑", HudTextRole.PRIMARY)
        )), content.rows());
    }

    @Test
    void preservesMinimalLayoutDividerAndPixelGaps() {
        HudPanelContent content = MainPanelContent.compose(
            CoordinateDisplayMode.DECIMAL_ONLY,
            WorldNameDisplay.BEHIND,
            false,
            false,
            VALUES,
            HudLayout.forPanel(false)
        );

        assertEquals(List.of(row(
            text("X ", HudTextRole.X), text("12.3", HudTextRole.PRIMARY),
            gap(5),
            text("Y ", HudTextRole.Y), text("64.0", HudTextRole.PRIMARY),
            gap(5),
            text("Z ", HudTextRole.Z), text("-8.5", HudTextRole.PRIMARY),
            text("/", HudTextRole.ACCENT),
            truncatable("Overworld", HudTextRole.WORLD)
        )), content.rows());
    }

    @Test
    void producesNoRowsWhenEveryMainValueIsDisabled() {
        HudPanelContent content = MainPanelContent.compose(
            CoordinateDisplayMode.HIDDEN,
            WorldNameDisplay.OFF,
            false,
            false,
            VALUES,
            HudLayout.forPanel(true)
        );

        assertTrue(content.isEmpty());
        assertEquals(0, content.rowCount());
    }

    @Test
    void coversTheCompleteMainRowPlanMatrix() {
        for (CoordinateDisplayMode coordinateDisplay : CoordinateDisplayMode.values()) {
            for (WorldNameDisplay worldDisplay : WorldNameDisplay.values()) {
                for (boolean directionEnabled : List.of(false, true)) {
                    for (boolean anglesEnabled : List.of(false, true)) {
                        for (boolean lensEnabled : List.of(false, true)) {
                            MainPanelContent.Values values = lensEnabled
                                ? valuesWithLens()
                                : VALUES;
                            HudPanelContent content = MainPanelContent.compose(
                                coordinateDisplay,
                                worldDisplay,
                                directionEnabled,
                                anglesEnabled,
                                values,
                                HudLayout.forPanel(true)
                            );

                            int expectedRows = (coordinateDisplay.showsDecimal() ? 1 : 0)
                                + (coordinateDisplay.showsBlock() ? 1 : 0)
                                + (coordinateDisplay == CoordinateDisplayMode.HIDDEN
                                    && worldDisplay.showsWorld() ? 1 : 0)
                                + (lensEnabled ? 1 : 0)
                                + (directionEnabled || anglesEnabled ? 1 : 0);
                            String caseDescription = coordinateDisplay + "/" + worldDisplay
                                + "/direction=" + directionEnabled
                                + "/angles=" + anglesEnabled
                                + "/lens=" + lensEnabled;

                            assertEquals(expectedRows, content.rowCount(), caseDescription);
                            assertEquals(expectedRows == 0, content.isEmpty(), caseDescription);
                            assertTrue(
                                content.rows().stream().noneMatch(row -> row.parts().isEmpty()),
                                caseDescription
                            );
                        }
                    }
                }
            }
        }
    }

    @Test
    void placesTheCoordinateLensBetweenCoordinatesAndViewInformation() {
        MainPanelContent.Values values = valuesWithLens();

        HudPanelContent content = MainPanelContent.compose(
            CoordinateDisplayMode.DECIMAL_ONLY,
            WorldNameDisplay.OFF,
            true,
            false,
            values,
            HudLayout.forPanel(true)
        );

        assertEquals(List.of(
            row(
                text("X ", HudTextRole.X), text("800.0", HudTextRole.PRIMARY),
                gap(9),
                text("Y ", HudTextRole.Y), text("64.0", HudTextRole.PRIMARY),
                gap(9),
                text("Z ", HudTextRole.Z), text("-80.0", HudTextRole.PRIMARY)
            ),
            row(
                text("NETHER", HudTextRole.WORLD),
                text(" ≈ ", HudTextRole.ACCENT),
                text("X ", HudTextRole.X), text("100.0", HudTextRole.PRIMARY),
                gap(9),
                text("Z ", HudTextRole.Z), text("-10.0", HudTextRole.PRIMARY)
            ),
            row(text("North", HudTextRole.DIRECTION))
        ), content.rows());
    }

    private static MainPanelContent.Values valuesWithLens() {
        return new MainPanelContent.Values(
            "800.0",
            "64.0",
            "-80.0",
            "800",
            "64",
            "-80",
            "Overworld",
            "North",
            "143°",
            "52°↑",
            Optional.of(new MainPanelContent.LensValues("NETHER", "100.0", "-10.0"))
        );
    }

    private static HudRow row(HudRowPart... parts) {
        return HudRow.of(parts);
    }

    private static HudText text(String value, HudTextRole role) {
        return HudText.of(value, role);
    }

    private static HudText truncatable(String value, HudTextRole role) {
        return HudText.truncatable(value, role);
    }

    private static HudGap gap(int width) {
        return new HudGap(width);
    }
}
