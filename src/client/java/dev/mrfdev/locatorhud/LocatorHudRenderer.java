package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.BackgroundOpacity;
import dev.mrfdev.locatorhud.config.LocatorHudConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class LocatorHudRenderer {
    private final LocatorHudConfig config;
    private final ClientHudSampler sampler;
    private final HudPanelPlacements panelPlacements;

    public LocatorHudRenderer(
        LocatorHudConfig config,
        ClientHudSampler sampler,
        HudPanelPlacements panelPlacements
    ) {
        this.config = Objects.requireNonNull(config, "config");
        this.sampler = Objects.requireNonNull(sampler, "sampler");
        this.panelPlacements = Objects.requireNonNull(panelPlacements, "panelPlacements");
    }

    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (!this.config.enabled() || client.font == null) {
            this.panelPlacements.update(null, null);
            return;
        }
        HudSnapshot snapshot = this.sampler.snapshot(client);
        if (snapshot == null) {
            this.panelPlacements.update(null, null);
            return;
        }

        Font font = client.font;
        HudPaletteColors palette = snapshot.paletteColors();
        PanelGeometry.Screen screen = new PanelGeometry.Screen(graphics.guiWidth(), graphics.guiHeight());
        PanelGeometry.Placement mainPlacement = this.config.mainPanelEnabled()
            ? renderMainPanel(graphics, font, palette, snapshot, screen)
            : null;
        PanelGeometry.Placement detailsPlacement = this.config.detailsPanelEnabled()
            ? renderDetailsPanel(graphics, font, palette, snapshot, screen, mainPlacement)
            : null;
        this.panelPlacements.update(mainPlacement, detailsPlacement);
    }

    private PanelGeometry.Placement renderMainPanel(
        GuiGraphicsExtractor graphics,
        Font font,
        HudPaletteColors palette,
        HudSnapshot snapshot,
        PanelGeometry.Screen screen
    ) {
        HudLayout layout = HudLayout.forPanel(this.config.backgroundOpacity().drawsPanel());
        HudScale scale = this.config.hudScale();
        ViewDirectionDisplay directionDisplay = this.config.viewDirectionDisplay();
        boolean directionEnabled = directionDisplay.showsDirection();
        boolean anglesEnabled = this.config.viewAnglesEnabled();
        HudPanelContent content = MainPanelContent.compose(
            DebugInfoPolicy.coordinateDisplay(
                snapshot.reducedDebugInfo(),
                this.config.coordinateDisplay()
            ),
            this.config.worldNameDisplay(),
            directionEnabled,
            anglesEnabled,
            new MainPanelContent.Values(
                this.config.precision().format(snapshot.x()),
                this.config.precision().format(snapshot.y()),
                this.config.precision().format(snapshot.z()),
                CoordinatePrecision.BLOCK.format(snapshot.x()),
                CoordinatePrecision.BLOCK.format(snapshot.y()),
                CoordinatePrecision.BLOCK.format(snapshot.z()),
                snapshot.world(),
                DirectionNameFormatter.format(
                    directionDisplay,
                    snapshot.directionName(),
                    snapshot.yaw()
                ),
                anglesEnabled
                    ? ViewAngleFormatter.horizontal(snapshot.yaw(), this.config.viewAnglePrecision())
                    : "",
                anglesEnabled
                    ? ViewAngleFormatter.vertical(snapshot.pitch(), this.config.viewAnglePrecision())
                    : "",
                coordinateLens(snapshot)
            ),
            layout
        );
        if (content.isEmpty()) {
            return null;
        }
        FittedPanelContent fitted = fitPanelContent(
            font,
            content,
            layout,
            PanelGeometry.maximumContentWidth(screen, layout, scale.percentage()),
            this.config.mainPanelWidthLimits()
        );
        content = fitted.content();
        PanelGeometry.PanelSize size = PanelGeometry.measure(
            layout,
            fitted.contentWidth(),
            font.lineHeight,
            content.rowCount(),
            scale.percentage()
        );
        PanelGeometry.Placement placement = PanelGeometry.place(
            screen,
            this.config.corner(),
            layout,
            size,
            this.config.mainPanelOffset()
        );

        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate(placement.x(), placement.y());
            graphics.pose().scale(scale.factor(), scale.factor());
            drawPanelChrome(
                graphics,
                palette,
                layout,
                this.config.backgroundOpacity(),
                size.unscaledWidth(),
                size.unscaledHeight()
            );
            drawContent(graphics, font, palette, layout, content);
        } finally {
            graphics.pose().popMatrix();
        }
        return placement;
    }

    private Optional<MainPanelContent.LensValues> coordinateLens(HudSnapshot snapshot) {
        if (!DebugInfoPolicy.coordinateLensEnabled(
            snapshot.reducedDebugInfo(),
            this.config.coordinateLensEnabled()
        )) {
            return Optional.empty();
        }
        CoordinatePrecision precision = this.config.precision();
        return OverworldNetherLens.project(
            snapshot.sourceDimension(),
            snapshot.x(),
            snapshot.z()
        ).map(projection -> new MainPanelContent.LensValues(
            Component.translatable(projection.destination().translationKey()).getString(),
            precision.format(projection.x()),
            precision.format(projection.z())
        ));
    }

    private PanelGeometry.Placement renderDetailsPanel(
        GuiGraphicsExtractor graphics,
        Font font,
        HudPaletteColors palette,
        HudSnapshot snapshot,
        PanelGeometry.Screen screen,
        PanelGeometry.Placement mainPlacement
    ) {
        CrosshairTargets targets = snapshot.targets();
        TargetNameMode targetNameMode = this.config.targetNameMode();
        HudLayout layout = HudLayout.forPanel(this.config.detailsBackgroundOpacity().drawsPanel());
        HudScale scale = this.config.detailsHudScale();
        HudPanelContent content = DetailsPanelContent.compose(
            new DetailsPanelContent.Settings(
                this.config.biomeEnabled(),
                this.config.biomeTransitionEnabled(),
                this.config.movementSpeedEnabled(),
                DebugInfoPolicy.targetDetailEnabled(
                    snapshot.reducedDebugInfo(),
                    this.config.targetBlockEnabled()
                ),
                DebugInfoPolicy.targetDetailEnabled(
                    snapshot.reducedDebugInfo(),
                    this.config.targetFluidEnabled()
                ),
                DebugInfoPolicy.targetDetailEnabled(
                    snapshot.reducedDebugInfo(),
                    this.config.targetEntityEnabled()
                ),
                this.config.autoHideEmptyTargetValues()
            ),
            new DetailsPanelContent.Values(
                snapshot.biome(),
                snapshot.biomeTransition(),
                MovementSpeedFormatter.format(snapshot.movementSpeed()),
                targets.block().display(targetNameMode),
                targets.fluid().display(targetNameMode),
                targets.entity().display(targetNameMode)
            )
        );
        if (content.isEmpty()) {
            return null;
        }
        FittedPanelContent fitted = fitPanelContent(
            font,
            content,
            layout,
            PanelGeometry.maximumContentWidth(screen, layout, scale.percentage()),
            this.config.detailsPanelWidthLimits()
        );
        content = fitted.content();
        PanelGeometry.PanelSize size = PanelGeometry.measure(
            layout,
            fitted.contentWidth(),
            font.lineHeight,
            content.rowCount(),
            scale.percentage()
        );
        boolean autoStack = mainPlacement != null
            && this.config.detailsCorner() == this.config.corner()
            && this.config.detailsPanelOffset().equals(PanelGeometry.Offset.ZERO);
        PanelGeometry.Offset placementOffset = autoStack
            ? this.config.mainPanelOffset()
            : this.config.detailsPanelOffset();
        PanelGeometry.Placement placement = PanelGeometry.place(
            screen,
            this.config.detailsCorner(),
            layout,
            size,
            placementOffset
        );
        if (autoStack) {
            placement = PanelGeometry.stack(
                screen,
                placement,
                mainPlacement,
                layout,
                this.config.detailsCorner(),
                PanelGeometry.STACKED_PANEL_GAP
            );
        }

        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate(placement.x(), placement.y());
            graphics.pose().scale(scale.factor(), scale.factor());
            drawPanelChrome(
                graphics,
                palette,
                layout,
                this.config.detailsBackgroundOpacity(),
                size.unscaledWidth(),
                size.unscaledHeight()
            );
            drawContent(graphics, font, palette, layout, content);
        } finally {
            graphics.pose().popMatrix();
        }
        return placement;
    }

    private void drawContent(
        GuiGraphicsExtractor graphics,
        Font font,
        HudPaletteColors palette,
        HudLayout layout,
        HudPanelContent content
    ) {
        int textX = layout.accentWidth() + layout.horizontalPadding();
        int rowY = layout.verticalPadding();
        for (HudRow row : content.rows()) {
            int cursorX = textX;
            for (HudRowPart part : row.parts()) {
                if (part instanceof HudGap gap) {
                    cursorX += gap.width();
                } else if (part instanceof HudText text) {
                    graphics.text(
                        font,
                        text.text(),
                        cursorX,
                        rowY,
                        roleColor(palette, text.role()),
                        this.config.textShadow()
                    );
                    cursorX += font.width(text.text());
                }
            }
            rowY += font.lineHeight + layout.rowGap();
        }
    }

    private static int roleColor(HudPaletteColors palette, HudTextRole role) {
        return switch (role) {
            case PRIMARY -> palette.primary();
            case SECONDARY -> palette.secondary();
            case ACCENT -> palette.accent();
            case X -> palette.x();
            case Y -> palette.y();
            case Z -> palette.z();
            case DIRECTION -> palette.direction();
            case WORLD -> palette.world();
            case BIOME -> palette.biome();
            case TARGET_ENTITY_LABEL -> palette.targetEntityLabel();
        };
    }

    private void drawPanelChrome(
        GuiGraphicsExtractor graphics,
        HudPaletteColors palette,
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

    private static HudPanelContent fitContent(Font font, HudPanelContent content, int maximumWidth) {
        List<HudRow> fittedRows = new ArrayList<>(content.rowCount());
        for (HudRow row : content.rows()) {
            fittedRows.add(fitRow(font, row, maximumWidth));
        }
        return new HudPanelContent(fittedRows);
    }

    private static FittedPanelContent fitPanelContent(
        Font font,
        HudPanelContent content,
        HudLayout layout,
        int screenMaximumContentWidth,
        PanelWidthLimits widthLimits
    ) {
        int constrainedWidth = widthLimits.constrainContentWidth(
            contentWidth(font, content),
            screenMaximumContentWidth,
            layout
        );
        HudPanelContent fittedContent = fitContent(font, content, constrainedWidth);
        return new FittedPanelContent(
            fittedContent,
            Math.max(constrainedWidth, contentWidth(font, fittedContent))
        );
    }

    private static HudRow fitRow(Font font, HudRow row, int maximumWidth) {
        List<HudRowPart> fittedParts = new ArrayList<>(row.parts());
        int width = rowWidth(font, row);
        for (int index = 0; index < fittedParts.size() && width > maximumWidth; index++) {
            HudRowPart part = fittedParts.get(index);
            if (!(part instanceof HudText text) || !text.truncatable()) {
                continue;
            }
            int originalTextWidth = font.width(text.text());
            int availableWidth = maximumWidth - (width - originalTextWidth);
            HudText fittedText = text.withText(truncate(font, text.text(), availableWidth));
            fittedParts.set(index, fittedText);
            width += font.width(fittedText.text()) - originalTextWidth;
        }
        return new HudRow(fittedParts);
    }

    private static int contentWidth(Font font, HudPanelContent content) {
        int maximumWidth = 0;
        for (HudRow row : content.rows()) {
            maximumWidth = Math.max(maximumWidth, rowWidth(font, row));
        }
        return maximumWidth;
    }

    private static int rowWidth(Font font, HudRow row) {
        int width = 0;
        for (HudRowPart part : row.parts()) {
            if (part instanceof HudGap gap) {
                width += gap.width();
            } else if (part instanceof HudText text) {
                width += font.width(text.text());
            }
        }
        return width;
    }

    private static String truncate(Font font, String value, int maximumWidth) {
        if (maximumWidth <= 0) {
            return "";
        }
        if (font.width(value) <= maximumWidth) {
            return value;
        }
        String ellipsis = "…";
        if (maximumWidth < font.width(ellipsis)) {
            return font.plainSubstrByWidth(value, maximumWidth);
        }
        int textWidth = Math.max(0, maximumWidth - font.width(ellipsis));
        return font.plainSubstrByWidth(value, textWidth) + ellipsis;
    }

    private record FittedPanelContent(HudPanelContent content, int contentWidth) {
        private FittedPanelContent {
            Objects.requireNonNull(content, "content");
            if (contentWidth < 0) {
                throw new IllegalArgumentException("contentWidth must not be negative");
            }
        }
    }

}
