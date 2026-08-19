package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class DetailsPanelContentTest {
    @Test
    void composesEveryEnabledRowInStableDisplayOrder() {
        HudPanelContent content = DetailsPanelContent.compose(
            new DetailsPanelContent.Settings(true, false, false, true, true, true, false),
            values("Jungle", "stone", "water", "zombie")
        );

        assertEquals(List.of(
            row("BIOME ", HudTextRole.SECONDARY, "Jungle", HudTextRole.BIOME),
            row("TB: ", HudTextRole.X, "stone", HudTextRole.PRIMARY),
            row("TF: ", HudTextRole.Z, "water", HudTextRole.PRIMARY),
            row("TE: ", HudTextRole.TARGET_ENTITY_LABEL, "zombie", HudTextRole.PRIMARY)
        ), content.rows());
        assertEquals(4, content.rowCount());
    }

    @Test
    void autoHideFiltersOnlyEmptyTargetRows() {
        HudPanelContent content = DetailsPanelContent.compose(
            new DetailsPanelContent.Settings(true, false, false, true, true, true, true),
            values("Jungle", TargetValue.EMPTY, "water", " ")
        );

        assertEquals(List.of(
            row("BIOME ", HudTextRole.SECONDARY, "Jungle", HudTextRole.BIOME),
            row("TF: ", HudTextRole.Z, "water", HudTextRole.PRIMARY)
        ), content.rows());
    }

    @Test
    void emptyEnabledTargetsRemainVisibleWhenAutoHideIsOff() {
        HudPanelContent content = DetailsPanelContent.compose(
            new DetailsPanelContent.Settings(false, false, false, true, false, false, false),
            values("Jungle", TargetValue.EMPTY, TargetValue.EMPTY, TargetValue.EMPTY)
        );

        assertEquals(List.of(
            row("TB: ", HudTextRole.X, TargetValue.EMPTY, HudTextRole.PRIMARY)
        ), content.rows());
    }

    @Test
    void producesNoRowsWhenAutoHideRemovesEveryEnabledTarget() {
        HudPanelContent content = DetailsPanelContent.compose(
            new DetailsPanelContent.Settings(false, false, false, true, true, true, true),
            values("Jungle", TargetValue.EMPTY, "", " ")
        );

        assertTrue(content.isEmpty());
        assertEquals(0, content.rowCount());
    }

    @Test
    void temporarilyReplacesTheBiomeValueWithATransitionThenShowsSpeed() {
        HudPanelContent content = DetailsPanelContent.compose(
            new DetailsPanelContent.Settings(true, true, true, false, false, false, false),
            new DetailsPanelContent.Values(
                "Jungle",
                Optional.of(new BiomeTransitionTracker.Notice("Plains", "Jungle")),
                "4.3 b/s",
                TargetValue.EMPTY,
                TargetValue.EMPTY,
                TargetValue.EMPTY
            )
        );

        assertEquals(List.of(
            HudRow.of(
                HudText.of("BIOME ", HudTextRole.SECONDARY),
                HudText.truncatable("Plains", HudTextRole.BIOME),
                HudText.of(" → ", HudTextRole.ACCENT),
                HudText.truncatable("Jungle", HudTextRole.BIOME)
            ),
            row("SPEED ", HudTextRole.SECONDARY, "4.3 b/s", HudTextRole.PRIMARY)
        ), content.rows());
    }

    @Test
    void coversTheCompleteDetailsRowPlanMatrix() {
        for (int settingsBits = 0; settingsBits < 128; settingsBits++) {
            boolean biomeEnabled = bit(settingsBits, 0);
            boolean transitionEnabled = bit(settingsBits, 1);
            boolean speedEnabled = bit(settingsBits, 2);
            boolean blockEnabled = bit(settingsBits, 3);
            boolean fluidEnabled = bit(settingsBits, 4);
            boolean entityEnabled = bit(settingsBits, 5);
            boolean autoHide = bit(settingsBits, 6);

            for (int targetBits = 0; targetBits < 8; targetBits++) {
                boolean blockPresent = bit(targetBits, 0);
                boolean fluidPresent = bit(targetBits, 1);
                boolean entityPresent = bit(targetBits, 2);
                DetailsPanelContent.Values values = new DetailsPanelContent.Values(
                    "Jungle",
                    Optional.of(new BiomeTransitionTracker.Notice("Plains", "Jungle")),
                    "4.3 b/s",
                    blockPresent ? "stone" : TargetValue.EMPTY,
                    fluidPresent ? "water" : TargetValue.EMPTY,
                    entityPresent ? "zombie" : TargetValue.EMPTY
                );

                HudPanelContent content = DetailsPanelContent.compose(
                    new DetailsPanelContent.Settings(
                        biomeEnabled,
                        transitionEnabled,
                        speedEnabled,
                        blockEnabled,
                        fluidEnabled,
                        entityEnabled,
                        autoHide
                    ),
                    values
                );

                int expectedRows = (biomeEnabled || transitionEnabled ? 1 : 0)
                    + (speedEnabled ? 1 : 0)
                    + (blockEnabled && (!autoHide || blockPresent) ? 1 : 0)
                    + (fluidEnabled && (!autoHide || fluidPresent) ? 1 : 0)
                    + (entityEnabled && (!autoHide || entityPresent) ? 1 : 0);
                String caseDescription = "settings=" + settingsBits + ", targets=" + targetBits;

                assertEquals(expectedRows, content.rowCount(), caseDescription);
                assertEquals(expectedRows == 0, content.isEmpty(), caseDescription);
                assertFalse(
                    content.rows().stream().anyMatch(row -> row.parts().isEmpty()),
                    caseDescription
                );
            }
        }
    }

    private static boolean bit(int value, int index) {
        return (value & (1 << index)) != 0;
    }

    private static DetailsPanelContent.Values values(
        String biome,
        String targetBlock,
        String targetFluid,
        String targetEntity
    ) {
        return new DetailsPanelContent.Values(
            biome,
            Optional.empty(),
            "0.0 b/s",
            targetBlock,
            targetFluid,
            targetEntity
        );
    }

    private static HudRow row(
        String label,
        HudTextRole labelRole,
        String value,
        HudTextRole valueRole
    ) {
        return HudRow.of(
            HudText.of(label, labelRole),
            HudText.truncatable(value, valueRole)
        );
    }
}
