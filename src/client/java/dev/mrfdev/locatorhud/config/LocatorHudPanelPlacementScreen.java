package dev.mrfdev.locatorhud.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.mrfdev.locatorhud.HudLayout;
import dev.mrfdev.locatorhud.HudPanelPlacements;
import dev.mrfdev.locatorhud.PanelGeometry;
import dev.mrfdev.locatorhud.PanelGeometry.Offset;
import dev.mrfdev.locatorhud.PanelGeometry.PanelSize;
import dev.mrfdev.locatorhud.PanelGeometry.Placement;
import dev.mrfdev.locatorhud.PanelPlacementPolicy;
import java.time.Duration;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class LocatorHudPanelPlacementScreen extends Screen {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 4;
    private static final int FOOTER_MARGIN = 8;
    private static final int FALLBACK_WIDTH = 140;
    private static final int FALLBACK_HEIGHT = 28;
    private static final int MAIN_COLOR = 0xFFFFD166;
    private static final int DETAILS_COLOR = 0xFF6EDFF6;
    private static final int SELECTED_COLOR = 0xFFFFFFFF;
    private static final int LABEL_BACKGROUND = 0xC0000000;
    private static final int INSTRUCTION_BACKGROUND = 0xB0000000;
    private static final Duration TOOLTIP_DELAY = Duration.ofSeconds(1);

    private final Screen parent;
    private final LocatorHudConfig config;
    private final HudPanelPlacements panelPlacements;
    private DragTarget dragging;
    private Placement dragPreview;
    private double grabOffsetX;
    private double grabOffsetY;

    public LocatorHudPanelPlacementScreen(
        Screen parent,
        LocatorHudConfig config,
        HudPanelPlacements panelPlacements
    ) {
        super(Component.translatable("screen.locatorhud.panel_placement.title"));
        this.parent = Objects.requireNonNull(parent, "parent");
        this.config = Objects.requireNonNull(config, "config");
        this.panelPlacements = Objects.requireNonNull(panelPlacements, "panelPlacements");
    }

    @Override
    protected void init() {
        int availableWidth = Math.max(2, this.width - FOOTER_MARGIN * 2 - BUTTON_GAP);
        int buttonWidth = Math.max(1, Math.min(BUTTON_WIDTH, availableWidth / 2));
        int totalWidth = buttonWidth * 2 + BUTTON_GAP;
        int x = (this.width - totalWidth) / 2;
        int y = Math.max(0, this.height - BUTTON_HEIGHT - FOOTER_MARGIN);
        addRenderableWidget(withTooltip(
            Button.builder(
                Component.translatable("button.locatorhud.reset_positions"),
                button -> {
                    this.config.resetPanelPlacements();
                    this.dragPreview = null;
                }
            ).bounds(x, y, buttonWidth, BUTTON_HEIGHT).build(),
            "tooltip.locatorhud.reset_positions"
        ));
        addRenderableWidget(withTooltip(
            Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(x + buttonWidth + BUTTON_GAP, y, buttonWidth, BUTTON_HEIGHT)
                .build(),
            "tooltip.locatorhud.panel_placement_done"
        ));
    }

    @Override
    public void extractRenderState(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        PanelFrames frames = frames();
        drawFrame(graphics, frames.main(), MAIN_COLOR);
        drawFrame(graphics, frames.details(), DETAILS_COLOR);
        drawInstructions(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        PanelFrames frames = frames();
        PanelFrame selected = frames.details().contains(event.x(), event.y())
            ? frames.details()
            : frames.main().contains(event.x(), event.y()) ? frames.main() : null;
        if (selected == null) {
            return false;
        }

        this.dragging = selected.target();
        this.dragPreview = selected.placement();
        this.grabOffsetX = event.x() - selected.placement().x();
        this.grabOffsetY = event.y() - selected.placement().y();
        setDragging(true);
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.dragging == null || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return super.mouseDragged(event, dragX, dragY);
        }

        Placement current = Objects.requireNonNull(this.dragPreview, "dragPreview");
        PanelSize size = new PanelSize(
            current.width(),
            current.height(),
            current.width(),
            current.height()
        );
        PanelPlacementPolicy.Result result = PanelPlacementPolicy.resolve(
            screen(),
            layout(this.dragging),
            size,
            (int) Math.round(event.x() - this.grabOffsetX),
            (int) Math.round(event.y() - this.grabOffsetY)
        );
        if (this.dragging == DragTarget.MAIN) {
            this.config.setMainPanelPlacement(result.corner(), result.offset());
        } else {
            this.config.setDetailsPanelPlacement(result.corner(), result.offset());
        }
        this.dragPreview = result.placement();
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.dragging == null || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return super.mouseReleased(event);
        }
        this.dragging = null;
        this.dragPreview = null;
        setDragging(false);
        this.config.flushPendingSave();
        return true;
    }

    @Override
    public void onClose() {
        this.config.flushPendingSave();
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private PanelFrames frames() {
        HudPanelPlacements.Snapshot rendered = this.panelPlacements.snapshot();
        Placement main = this.dragging == DragTarget.MAIN && this.dragPreview != null
            ? this.dragPreview
            : rendered.main();
        if (main == null) {
            main = fallback(DragTarget.MAIN);
        }

        Placement details = this.dragging == DragTarget.DETAILS && this.dragPreview != null
            ? this.dragPreview
            : rendered.details();
        if (details == null) {
            details = fallback(DragTarget.DETAILS);
            if (this.config.detailsCorner() == this.config.corner()
                && this.config.detailsPanelOffset().equals(Offset.ZERO)) {
                details = PanelGeometry.stack(
                    screen(),
                    details,
                    main,
                    layout(DragTarget.DETAILS),
                    this.config.detailsCorner(),
                    PanelGeometry.STACKED_PANEL_GAP
                );
            }
        }
        return new PanelFrames(
            new PanelFrame(DragTarget.MAIN, main),
            new PanelFrame(DragTarget.DETAILS, details)
        );
    }

    private Placement fallback(DragTarget target) {
        HudLayout layout = layout(target);
        int width = Math.max(1, Math.min(FALLBACK_WIDTH, this.width));
        int height = Math.max(1, Math.min(FALLBACK_HEIGHT, this.height));
        PanelSize size = new PanelSize(width, height, width, height);
        if (target == DragTarget.MAIN) {
            return PanelGeometry.place(
                screen(),
                this.config.corner(),
                layout,
                size,
                this.config.mainPanelOffset()
            );
        }
        Offset detailsOffset = this.config.detailsCorner() == this.config.corner()
            && this.config.detailsPanelOffset().equals(Offset.ZERO)
                ? this.config.mainPanelOffset()
                : this.config.detailsPanelOffset();
        return PanelGeometry.place(
            screen(),
            this.config.detailsCorner(),
            layout,
            size,
            detailsOffset
        );
    }

    private HudLayout layout(DragTarget target) {
        boolean drawsPanel = target == DragTarget.MAIN
            ? this.config.backgroundOpacity().drawsPanel()
            : this.config.detailsBackgroundOpacity().drawsPanel();
        return HudLayout.forPanel(drawsPanel);
    }

    private PanelGeometry.Screen screen() {
        return new PanelGeometry.Screen(this.width, this.height);
    }

    private void drawFrame(GuiGraphicsExtractor graphics, PanelFrame frame, int color) {
        Placement placement = frame.placement();
        int outlineColor = this.dragging == frame.target() ? SELECTED_COLOR : color;
        graphics.outline(
            placement.x() - 2,
            placement.y() - 2,
            placement.width() + 4,
            placement.height() + 4,
            outlineColor
        );
        Component label = Component.translatable(frame.target().labelKey());
        int labelWidth = this.font.width(label) + 8;
        int labelX = Math.max(0, Math.min(placement.x(), this.width - labelWidth));
        int preferredY = placement.y() >= this.font.lineHeight + 6
            ? placement.y() - this.font.lineHeight - 5
            : placement.y() + placement.height() + 3;
        int labelY = Math.max(0, Math.min(preferredY, this.height - this.font.lineHeight - 4));
        graphics.fill(
            labelX,
            labelY,
            labelX + labelWidth,
            labelY + this.font.lineHeight + 4,
            LABEL_BACKGROUND
        );
        graphics.text(this.font, label, labelX + 4, labelY + 2, outlineColor, true);
    }

    private void drawInstructions(GuiGraphicsExtractor graphics) {
        Component instructions = Component.translatable("screen.locatorhud.panel_placement.help");
        int textWidth = this.font.width(instructions);
        int boxWidth = Math.min(this.width, textWidth + 12);
        int x = Math.max(0, (this.width - boxWidth) / 2);
        graphics.fill(x, 2, x + boxWidth, this.font.lineHeight + 8, INSTRUCTION_BACKGROUND);
        graphics.centeredText(this.font, instructions, this.width / 2, 6, 0xFFFFFFFF);
    }

    private static <T extends AbstractWidget> T withTooltip(T widget, String translationKey) {
        widget.setTooltip(Tooltip.create(Component.translatable(translationKey)));
        widget.setTooltipDelay(TOOLTIP_DELAY);
        return widget;
    }

    private enum DragTarget {
        MAIN("screen.locatorhud.panel_placement.main"),
        DETAILS("screen.locatorhud.panel_placement.details");

        private final String labelKey;

        DragTarget(String labelKey) {
            this.labelKey = labelKey;
        }

        String labelKey() {
            return this.labelKey;
        }
    }

    private record PanelFrame(DragTarget target, Placement placement) {
        private PanelFrame {
            Objects.requireNonNull(target, "target");
            Objects.requireNonNull(placement, "placement");
        }

        boolean contains(double x, double y) {
            return x >= this.placement.x()
                && x < (long) this.placement.x() + this.placement.width()
                && y >= this.placement.y()
                && y < (long) this.placement.y() + this.placement.height();
        }
    }

    private record PanelFrames(PanelFrame main, PanelFrame details) {
    }
}
