package dev.mrfdev.locatorhud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DetailsPanelContent {
    private DetailsPanelContent() {
    }

    public static HudPanelContent compose(Settings settings, Values values) {
        Objects.requireNonNull(settings, "settings");
        Objects.requireNonNull(values, "values");

        Optional<BiomeTransitionTracker.Notice> biomeTransition =
            settings.biomeTransitionEnabled() ? values.biomeTransition() : Optional.empty();
        DetailsRowVisibility visibleRows = DetailsRowVisibility.resolve(
            settings.biomeEnabled() || biomeTransition.isPresent(),
            settings.targetBlockEnabled(),
            values.targetBlock(),
            settings.targetFluidEnabled(),
            values.targetFluid(),
            settings.targetEntityEnabled(),
            values.targetEntity(),
            settings.autoHideEmptyValues()
        );

        List<HudRow> rows = new ArrayList<>();
        if (visibleRows.biome()) {
            rows.add(biomeTransition
                .map(DetailsPanelContent::biomeTransitionRow)
                .orElseGet(() -> valueRow(
                    "BIOME ",
                    HudTextRole.SECONDARY,
                    values.biome(),
                    HudTextRole.BIOME
                )));
        }
        if (settings.movementSpeedEnabled()) {
            rows.add(valueRow(
                "SPEED ",
                HudTextRole.SECONDARY,
                values.movementSpeed(),
                HudTextRole.PRIMARY
            ));
        }
        if (visibleRows.targetBlock()) {
            rows.add(valueRow("TB: ", HudTextRole.X, values.targetBlock(), HudTextRole.PRIMARY));
        }
        if (visibleRows.targetFluid()) {
            rows.add(valueRow("TF: ", HudTextRole.Z, values.targetFluid(), HudTextRole.PRIMARY));
        }
        if (visibleRows.targetEntity()) {
            rows.add(valueRow(
                "TE: ",
                HudTextRole.TARGET_ENTITY_LABEL,
                values.targetEntity(),
                HudTextRole.PRIMARY
            ));
        }
        return new HudPanelContent(rows);
    }

    private static HudRow biomeTransitionRow(BiomeTransitionTracker.Notice notice) {
        return HudRow.of(
            HudText.of("BIOME ", HudTextRole.SECONDARY),
            HudText.truncatable(notice.from(), HudTextRole.BIOME),
            HudText.of(" → ", HudTextRole.ACCENT),
            HudText.truncatable(notice.to(), HudTextRole.BIOME)
        );
    }

    private static HudRow valueRow(
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

    public record Settings(
        boolean biomeEnabled,
        boolean biomeTransitionEnabled,
        boolean movementSpeedEnabled,
        boolean targetBlockEnabled,
        boolean targetFluidEnabled,
        boolean targetEntityEnabled,
        boolean autoHideEmptyValues
    ) {
    }

    public record Values(
        String biome,
        Optional<BiomeTransitionTracker.Notice> biomeTransition,
        String movementSpeed,
        String targetBlock,
        String targetFluid,
        String targetEntity
    ) {
        public Values {
            Objects.requireNonNull(biome, "biome");
            Objects.requireNonNull(biomeTransition, "biomeTransition");
            Objects.requireNonNull(movementSpeed, "movementSpeed");
            Objects.requireNonNull(targetBlock, "targetBlock");
            Objects.requireNonNull(targetFluid, "targetFluid");
            Objects.requireNonNull(targetEntity, "targetEntity");
        }
    }
}
