package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.BackgroundOpacity;
import dev.mrfdev.locatorhud.config.ColorPalette;
import dev.mrfdev.locatorhud.config.HudCorner;
import dev.mrfdev.locatorhud.config.LocatorHudConfig;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;

public final class LocatorHudRenderer {
    private static final int STACKED_PANEL_GAP = 4;

    private final LocatorHudConfig config;

    public LocatorHudRenderer(LocatorHudConfig config) {
        this.config = config;
    }

    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (!this.config.enabled() || player == null || client.level == null || client.font == null) {
            return;
        }

        Font font = client.font;
        ColorPalette palette = this.config.palette();
        PanelPlacement mainPlacement = this.config.mainPanelEnabled()
            ? renderMainPanel(graphics, font, palette, client, player)
            : null;
        if (this.config.detailsPanelEnabled()) {
            renderDetailsPanel(graphics, font, palette, client, mainPlacement);
        }
    }

    private PanelPlacement renderMainPanel(
        GuiGraphicsExtractor graphics,
        Font font,
        ColorPalette palette,
        Minecraft client,
        LocalPlayer player
    ) {
        HudLayout layout = HudLayout.forPanel(this.config.backgroundOpacity().drawsPanel());
        CoordinateDisplayMode coordinateDisplay = this.config.coordinateDisplay();
        WorldNameDisplay worldDisplay = this.config.worldNameDisplay();
        List<ViewRowSegment> viewRowSegments = ViewRowSegment.forSettings(
            this.config.viewDirectionEnabled(),
            this.config.viewAnglesEnabled()
        );
        int rowCount = coordinateDisplay.coreRows(worldDisplay, !viewRowSegments.isEmpty());
        if (rowCount == 0) {
            return null;
        }
        boolean worldWithDecimal = coordinateDisplay.worldSharesDecimalRow(worldDisplay);
        boolean worldWithBlock = coordinateDisplay.worldSharesBlockRow(worldDisplay);
        boolean standaloneWorld = coordinateDisplay.worldUsesOwnRow(worldDisplay);
        String decimalX = this.config.precision().format(player.getX());
        String decimalY = this.config.precision().format(player.getY());
        String decimalZ = this.config.precision().format(player.getZ());
        String blockX = CoordinatePrecision.BLOCK.format(player.getX());
        String blockY = CoordinatePrecision.BLOCK.format(player.getY());
        String blockZ = CoordinatePrecision.BLOCK.format(player.getZ());
        Identifier dimension = client.level.dimension().identifier();
        String world = WorldNameFormatter.fromIdentifier(dimension.getNamespace(), dimension.getPath());
        String direction = this.config.viewDirectionEnabled()
            ? DirectionNameFormatter.titleCase(player.getDirection().getName())
            : "";
        String horizontalAngle = this.config.viewAnglesEnabled()
            ? ViewAngleFormatter.horizontal(player.getYRot(), this.config.viewAnglePrecision())
            : "";
        String verticalAngle = this.config.viewAnglesEnabled()
            ? ViewAngleFormatter.vertical(player.getXRot(), this.config.viewAnglePrecision())
            : "";

        int decimalCoordinatesWidth = segmentWidth(font, "X", decimalX)
            + layout.segmentGap()
            + segmentWidth(font, "Y", decimalY)
            + layout.segmentGap()
            + segmentWidth(font, "Z", decimalZ);
        int blockCoordinatesWidth = font.width("BLOCK: ")
            + segmentWidth(font, "X", blockX)
            + layout.segmentGap()
            + segmentWidth(font, "Y", blockY)
            + layout.segmentGap()
            + segmentWidth(font, "Z", blockZ);
        int maximumContentWidth = maximumContentWidth(graphics, layout);
        int worldCoordinateOverhead = worldWithDecimal
            ? decimalCoordinatesWidth + font.width(layout.coordinateDivider())
            : worldWithBlock
                ? blockCoordinatesWidth + font.width(layout.coordinateDivider())
                : 0;
        world = worldDisplay.showsWorld()
            ? truncate(font, world, maximumContentWidth - worldCoordinateOverhead)
            : "";
        int worldWidth = font.width(world);
        int decimalWidth = decimalCoordinatesWidth
            + (worldWithDecimal ? font.width(layout.coordinateDivider()) + worldWidth : 0);
        int blockWidth = blockCoordinatesWidth
            + (worldWithBlock ? font.width(layout.coordinateDivider()) + worldWidth : 0);
        int viewRowWidth = 0;
        for (ViewRowSegment segment : viewRowSegments) {
            viewRowWidth += font.width(viewRowText(
                segment,
                direction,
                horizontalAngle,
                layout.detailDivider(),
                verticalAngle
            ));
        }

        int contentWidth = viewRowWidth;
        if (coordinateDisplay.showsDecimal()) {
            contentWidth = Math.max(contentWidth, decimalWidth);
        }
        if (coordinateDisplay.showsBlock()) {
            contentWidth = Math.max(contentWidth, blockWidth);
        }
        if (standaloneWorld) {
            contentWidth = Math.max(contentWidth, worldWidth);
        }
        int panelWidth = layout.panelWidth(contentWidth);
        int panelHeight = layout.panelHeight(font.lineHeight, rowCount);
        PanelPlacement placement = placePanel(
            graphics,
            this.config.corner(),
            this.config.hudScale(),
            layout,
            panelWidth,
            panelHeight
        );
        if (placement == null) {
            return null;
        }

        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate(placement.x(), placement.y());
            graphics.pose().scale(this.config.hudScale().factor(), this.config.hudScale().factor());
            drawPanelChrome(
                graphics,
                palette,
                layout,
                this.config.backgroundOpacity(),
                panelWidth,
                panelHeight
            );
            drawMainRows(
                graphics,
                font,
                palette,
                layout,
                coordinateDisplay,
                worldDisplay,
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
                viewRowSegments
            );
        } finally {
            graphics.pose().popMatrix();
        }
        return placement;
    }

    private void renderDetailsPanel(
        GuiGraphicsExtractor graphics,
        Font font,
        ColorPalette palette,
        Minecraft client,
        PanelPlacement mainPlacement
    ) {
        CrosshairTargets targets = CrosshairTargets.capture(
            client,
            this.config.targetBlockEnabled(),
            this.config.targetFluidEnabled(),
            this.config.targetEntityEnabled()
        );
        DetailsRowVisibility visibleRows = DetailsRowVisibility.resolve(
            this.config.biomeEnabled(),
            this.config.targetBlockEnabled(),
            targets.block(),
            this.config.targetFluidEnabled(),
            targets.fluid(),
            this.config.targetEntityEnabled(),
            targets.entity(),
            this.config.autoHideEmptyTargetValues()
        );
        if (visibleRows.isEmpty()) {
            return;
        }

        HudLayout layout = HudLayout.forPanel(this.config.detailsBackgroundOpacity().drawsPanel());
        int maximumContentWidth = maximumContentWidth(graphics, layout);
        String biome = visibleRows.biome()
            ? truncate(
                font,
                client.level.getBiome(client.player.blockPosition())
                    .unwrapKey()
                    .map(key -> key.identifier())
                    .map(identifier -> WorldNameFormatter.fromIdentifier(identifier.getNamespace(), identifier.getPath()))
                    .orElse("Unknown"),
                maximumContentWidth - font.width("BIOME ")
            )
            : "";
        String targetBlock = visibleRows.targetBlock()
            ? truncate(font, targets.block(), maximumContentWidth - font.width("TB: "))
            : "";
        String targetFluid = visibleRows.targetFluid()
            ? truncate(font, targets.fluid(), maximumContentWidth - font.width("TF: "))
            : "";
        String targetEntity = visibleRows.targetEntity()
            ? truncate(font, targets.entity(), maximumContentWidth - font.width("TE: "))
            : "";

        int contentWidth = 0;
        if (visibleRows.biome()) {
            contentWidth = Math.max(contentWidth, font.width("BIOME ") + font.width(biome));
        }
        if (visibleRows.targetBlock()) {
            contentWidth = Math.max(contentWidth, segmentWidth(font, "TB:", targetBlock));
        }
        if (visibleRows.targetFluid()) {
            contentWidth = Math.max(contentWidth, segmentWidth(font, "TF:", targetFluid));
        }
        if (visibleRows.targetEntity()) {
            contentWidth = Math.max(contentWidth, segmentWidth(font, "TE:", targetEntity));
        }

        int panelWidth = layout.panelWidth(contentWidth);
        int panelHeight = layout.panelHeight(font.lineHeight, visibleRows.rowCount());
        PanelPlacement placement = placePanel(
            graphics,
            this.config.detailsCorner(),
            this.config.detailsHudScale(),
            layout,
            panelWidth,
            panelHeight
        );
        if (placement == null) {
            return;
        }
        if (mainPlacement != null && this.config.detailsCorner() == this.config.corner()) {
            placement = stackWithMainPanel(graphics, placement, mainPlacement, layout, this.config.detailsCorner());
        }

        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate(placement.x(), placement.y());
            graphics.pose().scale(
                this.config.detailsHudScale().factor(),
                this.config.detailsHudScale().factor()
            );
            drawPanelChrome(
                graphics,
                palette,
                layout,
                this.config.detailsBackgroundOpacity(),
                panelWidth,
                panelHeight
            );
            drawDetailsRows(
                graphics,
                font,
                palette,
                layout,
                visibleRows,
                biome,
                targetBlock,
                targetFluid,
                targetEntity
            );
        } finally {
            graphics.pose().popMatrix();
        }
    }

    private void drawMainRows(
        GuiGraphicsExtractor graphics,
        Font font,
        ColorPalette palette,
        HudLayout layout,
        CoordinateDisplayMode coordinateDisplay,
        WorldNameDisplay worldDisplay,
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
        List<ViewRowSegment> viewRowSegments
    ) {
        int textX = layout.accentWidth() + layout.horizontalPadding();
        int rowY = layout.verticalPadding();
        if (coordinateDisplay.showsDecimal()) {
            drawCoordinateWorldRow(
                graphics,
                font,
                decimalX,
                decimalY,
                decimalZ,
                world,
                textX,
                rowY,
                palette,
                layout,
                coordinateDisplay.decimalRowSegments(worldDisplay),
                false
            );
            rowY += font.lineHeight + layout.rowGap();
        }
        if (coordinateDisplay.showsBlock()) {
            drawCoordinateWorldRow(
                graphics,
                font,
                blockX,
                blockY,
                blockZ,
                world,
                textX,
                rowY,
                palette,
                layout,
                coordinateDisplay.blockRowSegments(worldDisplay),
                true
            );
            rowY += font.lineHeight + layout.rowGap();
        }
        if (coordinateDisplay.worldUsesOwnRow(worldDisplay)) {
            graphics.text(font, world, textX, rowY, palette.world(), this.config.textShadow());
            rowY += font.lineHeight + layout.rowGap();
        }

        int cursorX = textX;
        for (ViewRowSegment segment : viewRowSegments) {
            String text = viewRowText(
                segment,
                direction,
                horizontalAngle,
                layout.detailDivider(),
                verticalAngle
            );
            int color = switch (segment) {
                case DIRECTION -> palette.direction();
                case OPEN_PARENTHESIS, CLOSE_PARENTHESIS -> palette.secondary();
                case HORIZONTAL_ANGLE, VERTICAL_ANGLE -> palette.primary();
                case DIVIDER -> palette.accent();
            };
            cursorX = drawText(graphics, font, text, cursorX, rowY, color);
        }
    }

    private static String viewRowText(
        ViewRowSegment segment,
        String direction,
        String horizontalAngle,
        String divider,
        String verticalAngle
    ) {
        return switch (segment) {
            case DIRECTION -> direction;
            case OPEN_PARENTHESIS -> " (";
            case HORIZONTAL_ANGLE -> horizontalAngle;
            case DIVIDER -> divider;
            case VERTICAL_ANGLE -> verticalAngle;
            case CLOSE_PARENTHESIS -> ")";
        };
    }

    private void drawCoordinateWorldRow(
        GuiGraphicsExtractor graphics,
        Font font,
        String xValue,
        String yValue,
        String zValue,
        String world,
        int x,
        int y,
        ColorPalette palette,
        HudLayout layout,
        List<CoordinateRowSegment> segments,
        boolean blockCoordinates
    ) {
        int cursorX = x;
        for (int index = 0; index < segments.size(); index++) {
            if (index > 0) {
                cursorX = drawText(graphics, font, layout.coordinateDivider(), cursorX, y, palette.accent());
            }
            if (segments.get(index) == CoordinateRowSegment.WORLD) {
                cursorX = drawText(graphics, font, world, cursorX, y, palette.world());
            } else if (blockCoordinates) {
                cursorX = drawBlockRow(
                    graphics,
                    font,
                    xValue,
                    yValue,
                    zValue,
                    cursorX,
                    y,
                    palette,
                    layout.segmentGap()
                );
            } else {
                cursorX = drawDecimalRow(
                    graphics,
                    font,
                    xValue,
                    yValue,
                    zValue,
                    cursorX,
                    y,
                    palette,
                    layout.segmentGap()
                );
            }
        }
    }

    private void drawDetailsRows(
        GuiGraphicsExtractor graphics,
        Font font,
        ColorPalette palette,
        HudLayout layout,
        DetailsRowVisibility visibleRows,
        String biome,
        String targetBlock,
        String targetFluid,
        String targetEntity
    ) {
        int textX = layout.accentWidth() + layout.horizontalPadding();
        int rowY = layout.verticalPadding();
        if (visibleRows.biome()) {
            graphics.text(font, "BIOME ", textX, rowY, palette.secondary(), this.config.textShadow());
            drawText(graphics, font, biome, textX + font.width("BIOME "), rowY, palette.biome());
            rowY += font.lineHeight + layout.rowGap();
        }
        if (visibleRows.targetBlock()) {
            drawSegment(graphics, font, "TB:", targetBlock, textX, rowY, palette.x(), palette.primary());
            rowY += font.lineHeight + layout.rowGap();
        }
        if (visibleRows.targetFluid()) {
            drawSegment(graphics, font, "TF:", targetFluid, textX, rowY, palette.z(), palette.primary());
            rowY += font.lineHeight + layout.rowGap();
        }
        if (visibleRows.targetEntity()) {
            drawSegment(
                graphics,
                font,
                "TE:",
                targetEntity,
                textX,
                rowY,
                palette.targetEntityLabel(),
                palette.primary()
            );
        }
    }

    private void drawPanelChrome(
        GuiGraphicsExtractor graphics,
        ColorPalette palette,
        HudLayout layout,
        BackgroundOpacity background,
        int panelWidth,
        int panelHeight
    ) {
        if (!layout.drawsPanel()) {
            return;
        }
        if (this.config.panelShadow()) {
            int shadowAlpha = Math.min(0x50, background.alpha());
            graphics.fill(1, 2, panelWidth + 1, panelHeight + 2, shadowAlpha << 24);
        }
        graphics.fill(0, 0, panelWidth, panelHeight, background.applyTo(palette.backgroundRgb()));
        graphics.fill(0, 0, layout.accentWidth(), panelHeight, palette.accent());
    }

    private int drawDecimalRow(
        GuiGraphicsExtractor graphics,
        Font font,
        String xValue,
        String yValue,
        String zValue,
        int x,
        int y,
        ColorPalette palette,
        int segmentGap
    ) {
        int cursorX = drawSegment(graphics, font, "X", xValue, x, y, palette.x(), palette.primary());
        cursorX = drawSegment(graphics, font, "Y", yValue, cursorX + segmentGap, y, palette.y(), palette.primary());
        return drawSegment(graphics, font, "Z", zValue, cursorX + segmentGap, y, palette.z(), palette.primary());
    }

    private int drawBlockRow(
        GuiGraphicsExtractor graphics,
        Font font,
        String xValue,
        String yValue,
        String zValue,
        int x,
        int y,
        ColorPalette palette,
        int segmentGap
    ) {
        graphics.text(font, "BLOCK: ", x, y, palette.secondary(), this.config.textShadow());
        int cursorX = x + font.width("BLOCK: ");
        cursorX = drawSegment(graphics, font, "X", xValue, cursorX, y, palette.x(), palette.primary());
        cursorX = drawSegment(graphics, font, "Y", yValue, cursorX + segmentGap, y, palette.y(), palette.primary());
        return drawSegment(graphics, font, "Z", zValue, cursorX + segmentGap, y, palette.z(), palette.primary());
    }

    private int drawText(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int color) {
        graphics.text(font, text, x, y, color, this.config.textShadow());
        return x + font.width(text);
    }

    private int drawSegment(
        GuiGraphicsExtractor graphics,
        Font font,
        String label,
        String value,
        int x,
        int y,
        int labelColor,
        int valueColor
    ) {
        graphics.text(font, label, x, y, labelColor, this.config.textShadow());
        int valueX = x + font.width(label + " ");
        graphics.text(font, value, valueX, y, valueColor, this.config.textShadow());
        return valueX + font.width(value);
    }

    private static int maximumContentWidth(GuiGraphicsExtractor graphics, HudLayout layout) {
        return graphics.guiWidth()
            - layout.margin() * 2
            - layout.horizontalPadding() * 2
            - layout.accentWidth();
    }

    private static PanelPlacement placePanel(
        GuiGraphicsExtractor graphics,
        HudCorner corner,
        HudScale scale,
        HudLayout layout,
        int panelWidth,
        int panelHeight
    ) {
        int scaledWidth = scale.scaleDimension(panelWidth);
        int scaledHeight = scale.scaleDimension(panelHeight);
        int x = corner.x(graphics.guiWidth(), scaledWidth, layout.margin());
        int y = corner.y(graphics.guiHeight(), scaledHeight, layout.margin());
        if (x < 0 || y < 0) {
            return null;
        }
        return new PanelPlacement(x, y, scaledWidth, scaledHeight);
    }

    private static PanelPlacement stackWithMainPanel(
        GuiGraphicsExtractor graphics,
        PanelPlacement details,
        PanelPlacement main,
        HudLayout detailsLayout,
        HudCorner corner
    ) {
        int y = corner.isTop()
            ? main.y() + main.height() + STACKED_PANEL_GAP
            : main.y() - details.height() - STACKED_PANEL_GAP;
        int maximumY = graphics.guiHeight() - details.height() - detailsLayout.margin();
        y = Math.max(detailsLayout.margin(), Math.min(y, maximumY));
        return new PanelPlacement(details.x(), y, details.width(), details.height());
    }

    private static int segmentWidth(Font font, String label, String value) {
        return font.width(label + " ") + font.width(value);
    }

    private static String truncate(Font font, String value, int maximumWidth) {
        if (maximumWidth <= 0) {
            return "";
        }
        if (font.width(value) <= maximumWidth) {
            return value;
        }
        String ellipsis = "…";
        int textWidth = Math.max(0, maximumWidth - font.width(ellipsis));
        return font.plainSubstrByWidth(value, textWidth) + ellipsis;
    }

    private record PanelPlacement(int x, int y, int width, int height) {
    }
}
