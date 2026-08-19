package dev.mrfdev.locatorhud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MainPanelContent {
    private MainPanelContent() {
    }

    public static HudPanelContent compose(
        CoordinateDisplayMode coordinateDisplay,
        WorldNameDisplay worldDisplay,
        boolean directionEnabled,
        boolean anglesEnabled,
        Values values,
        HudLayout layout
    ) {
        Objects.requireNonNull(coordinateDisplay, "coordinateDisplay");
        Objects.requireNonNull(worldDisplay, "worldDisplay");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(layout, "layout");

        List<HudRow> rows = new ArrayList<>();
        if (coordinateDisplay.showsDecimal()) {
            rows.add(coordinateWorldRow(
                coordinateDisplay.decimalRowSegments(worldDisplay),
                coordinateParts(values.decimalX(), values.decimalY(), values.decimalZ(), false, layout),
                values.world(),
                layout
            ));
        }
        if (coordinateDisplay.showsBlock()) {
            rows.add(coordinateWorldRow(
                coordinateDisplay.blockRowSegments(worldDisplay),
                coordinateParts(values.blockX(), values.blockY(), values.blockZ(), true, layout),
                values.world(),
                layout
            ));
        }
        if (coordinateDisplay.worldUsesOwnRow(worldDisplay)) {
            rows.add(HudRow.of(HudText.truncatable(values.world(), HudTextRole.WORLD)));
        }
        values.coordinateLens().ifPresent(lens -> rows.add(coordinateLensRow(lens, layout)));

        List<ViewRowSegment> viewSegments = ViewRowSegment.forSettings(directionEnabled, anglesEnabled);
        if (!viewSegments.isEmpty()) {
            rows.add(viewRow(viewSegments, values, layout));
        }
        return new HudPanelContent(rows);
    }

    private static HudRow coordinateWorldRow(
        List<CoordinateRowSegment> segments,
        List<HudRowPart> coordinateParts,
        String world,
        HudLayout layout
    ) {
        List<HudRowPart> parts = new ArrayList<>();
        for (int index = 0; index < segments.size(); index++) {
            if (index > 0) {
                parts.add(HudText.of(layout.coordinateDivider(), HudTextRole.ACCENT));
            }
            if (segments.get(index) == CoordinateRowSegment.WORLD) {
                parts.add(HudText.truncatable(world, HudTextRole.WORLD));
            } else {
                parts.addAll(coordinateParts);
            }
        }
        return new HudRow(parts);
    }

    private static List<HudRowPart> coordinateParts(
        String x,
        String y,
        String z,
        boolean blockCoordinates,
        HudLayout layout
    ) {
        List<HudRowPart> parts = new ArrayList<>();
        if (blockCoordinates) {
            parts.add(HudText.of("BLOCK: ", HudTextRole.SECONDARY));
        }
        addCoordinate(parts, "X ", x, HudTextRole.X);
        parts.add(new HudGap(layout.segmentGap()));
        addCoordinate(parts, "Y ", y, HudTextRole.Y);
        parts.add(new HudGap(layout.segmentGap()));
        addCoordinate(parts, "Z ", z, HudTextRole.Z);
        return List.copyOf(parts);
    }

    private static void addCoordinate(
        List<HudRowPart> parts,
        String label,
        String value,
        HudTextRole labelRole
    ) {
        parts.add(HudText.of(label, labelRole));
        parts.add(HudText.of(value, HudTextRole.PRIMARY));
    }

    private static HudRow coordinateLensRow(LensValues values, HudLayout layout) {
        List<HudRowPart> parts = new ArrayList<>();
        parts.add(HudText.of(values.destination(), HudTextRole.WORLD));
        parts.add(HudText.of(" ≈ ", HudTextRole.ACCENT));
        addCoordinate(parts, "X ", values.x(), HudTextRole.X);
        parts.add(new HudGap(layout.segmentGap()));
        addCoordinate(parts, "Z ", values.z(), HudTextRole.Z);
        return new HudRow(parts);
    }

    private static HudRow viewRow(
        List<ViewRowSegment> segments,
        Values values,
        HudLayout layout
    ) {
        List<HudRowPart> parts = new ArrayList<>();
        for (ViewRowSegment segment : segments) {
            parts.add(switch (segment) {
                case DIRECTION -> HudText.of(values.direction(), HudTextRole.DIRECTION);
                case OPEN_PARENTHESIS -> HudText.of(" (", HudTextRole.SECONDARY);
                case HORIZONTAL_ANGLE -> HudText.of(values.horizontalAngle(), HudTextRole.PRIMARY);
                case DIVIDER -> HudText.of(layout.detailDivider(), HudTextRole.ACCENT);
                case VERTICAL_ANGLE -> HudText.of(values.verticalAngle(), HudTextRole.PRIMARY);
                case CLOSE_PARENTHESIS -> HudText.of(")", HudTextRole.SECONDARY);
            });
        }
        return new HudRow(parts);
    }

    public record Values(
        String decimalX,
        String decimalY,
        String decimalZ,
        String blockX,
        String blockY,
        String blockZ,
        String world,
        String direction,
        String horizontalAngle,
        String verticalAngle,
        Optional<LensValues> coordinateLens
    ) {
        public Values(
            String decimalX,
            String decimalY,
            String decimalZ,
            String blockX,
            String blockY,
            String blockZ,
            String world,
            String direction,
            String horizontalAngle,
            String verticalAngle
        ) {
            this(
                decimalX,
                decimalY,
                decimalZ,
                blockX,
                blockY,
                blockZ,
                world,
                direction,
                horizontalAngle,
                verticalAngle,
                Optional.empty()
            );
        }

        public Values {
            Objects.requireNonNull(decimalX, "decimalX");
            Objects.requireNonNull(decimalY, "decimalY");
            Objects.requireNonNull(decimalZ, "decimalZ");
            Objects.requireNonNull(blockX, "blockX");
            Objects.requireNonNull(blockY, "blockY");
            Objects.requireNonNull(blockZ, "blockZ");
            Objects.requireNonNull(world, "world");
            Objects.requireNonNull(direction, "direction");
            Objects.requireNonNull(horizontalAngle, "horizontalAngle");
            Objects.requireNonNull(verticalAngle, "verticalAngle");
            Objects.requireNonNull(coordinateLens, "coordinateLens");
        }
    }

    public record LensValues(String destination, String x, String z) {
        public LensValues {
            Objects.requireNonNull(destination, "destination");
            Objects.requireNonNull(x, "x");
            Objects.requireNonNull(z, "z");
        }
    }
}
