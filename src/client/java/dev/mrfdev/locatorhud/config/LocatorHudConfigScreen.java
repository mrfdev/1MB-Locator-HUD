package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.LocatorHudClient;
import dev.mrfdev.locatorhud.ViewAnglePrecision;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class LocatorHudConfigScreen extends Screen {
    private static final int MAX_BUTTON_WIDTH = 240;
    private static final int BUTTON_HEIGHT = 16;
    private static final int ROW_SPACING = 16;
    private static final int COLUMN_GAP = 4;
    private static final int FOOTER_ROW = 12;
    private static final int KEY_COLOR = 0xA8A8A8;
    private static final int VALUE_COLOR = 0xFFFFFF;
    private static final int ENABLED_COLOR = 0x55FF55;
    private static final int DISABLED_COLOR = 0xFF7777;
    private static final int SECTION_COLOR = 0xFFD166;

    private final Screen parent;

    public LocatorHudConfigScreen(Screen parent) {
        super(Component.translatable("screen.locatorhud.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        LocatorHudConfig config = LocatorHudClient.instance().config();
        int buttonWidth = Math.min(MAX_BUTTON_WIDTH, (this.width - 12) / 2);
        int left = this.width / 2 - buttonWidth - COLUMN_GAP / 2;
        int right = this.width / 2 + COLUMN_GAP / 2;
        int contentHeight = ROW_SPACING * FOOTER_ROW + 4 + BUTTON_HEIGHT;
        int top = Math.max(20, (this.height - contentHeight) / 2);

        CycleButton<CoordinatePrecision> precisionButton = CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.precision()
        ).withValues(CoordinatePrecision.ONE_DECIMAL, CoordinatePrecision.TWO_DECIMALS).create(
            left,
            row(top, 6),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.decimal_precision"),
            (button, value) -> config.setPrecision(value)
        );
        precisionButton.active = config.coordinateDisplay().showsDecimal();

        CycleButton<ViewAnglePrecision> anglePrecisionButton = CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.viewAnglePrecision()
        ).withValues(ViewAnglePrecision.values()).create(
            left,
            row(top, 9),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.angle_decimals"),
            (button, value) -> config.setViewAnglePrecision(value)
        );
        anglePrecisionButton.active = config.viewAnglesEnabled();

        CycleButton<Boolean> panelShadowButton = onOffBuilder(config.panelShadow()).create(
            right,
            row(top, 1),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.panel_shadow"),
            (button, enabled) -> config.setPanelShadow(enabled)
        );
        panelShadowButton.active = hasVisibleBackground(config);

        addRenderableWidget(new StringWidget(
            left,
            top - 18,
            buttonWidth * 2 + COLUMN_GAP,
            16,
            this.title,
            this.font
        ));

        addRenderableWidget(onOffBuilder(config.enabled()).create(
            left,
            row(top, 0),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.enabled"),
            (button, enabled) -> config.setEnabled(enabled)
        ));
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.palette()
        ).withValues(ColorPalette.values()).create(
            right,
            row(top, 0),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.palette"),
            (button, value) -> config.setPalette(value)
        ));
        addRenderableWidget(onOffBuilder(config.textShadow()).create(
            left,
            row(top, 1),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.text_shadow"),
            (button, enabled) -> config.setTextShadow(enabled)
        ));
        addRenderableWidget(panelShadowButton);

        addSectionHeading(left, row(top, 2), buttonWidth, "section.locatorhud.main_panel");
        addSectionHeading(right, row(top, 2), buttonWidth, "section.locatorhud.details_panel");

        addRenderableWidget(onOffBuilder(config.mainPanelEnabled()).create(
            left,
            row(top, 3),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.main_panel_enabled"),
            (button, enabled) -> {
                config.setMainPanelEnabled(enabled);
                panelShadowButton.active = hasVisibleBackground(config);
            }
        ));
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.corner()
        ).withValues(HudCorner.values()).create(
            left,
            row(top, 4),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.position"),
            (button, value) -> config.setCorner(value)
        ));
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.coordinateDisplay()
        ).withValues(CoordinateDisplayMode.values()).create(
            left,
            row(top, 5),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.coordinate_display"),
            (button, value) -> {
                config.setCoordinateDisplay(value);
                precisionButton.active = value.showsDecimal();
            }
        ));
        addRenderableWidget(precisionButton);
        addRenderableWidget(onOffBuilder(config.worldNameEnabled()).create(
            left,
            row(top, 7),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.world_name"),
            (button, enabled) -> config.setWorldNameEnabled(enabled)
        ));
        addRenderableWidget(onOffBuilder(config.viewAnglesEnabled()).create(
            left,
            row(top, 8),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.view_angles"),
            (button, enabled) -> {
                config.setViewAnglesEnabled(enabled);
                anglePrecisionButton.active = enabled;
            }
        ));
        addRenderableWidget(anglePrecisionButton);
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.hudScale()
        ).withValues(HudScale.values()).create(
            left,
            row(top, 10),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.hud_size"),
            (button, value) -> config.setHudScale(value)
        ));
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.backgroundOpacity()
        ).withValues(BackgroundOpacity.values()).create(
            left,
            row(top, 11),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.background"),
            (button, value) -> {
                config.setBackgroundOpacity(value);
                panelShadowButton.active = hasVisibleBackground(config);
            }
        ));

        addRenderableWidget(onOffBuilder(config.detailsPanelEnabled()).create(
            right,
            row(top, 3),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.details_panel_enabled"),
            (button, enabled) -> {
                config.setDetailsPanelEnabled(enabled);
                panelShadowButton.active = hasVisibleBackground(config);
            }
        ));
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.detailsCorner()
        ).withValues(HudCorner.values()).create(
            right,
            row(top, 4),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.details_position"),
            (button, value) -> config.setDetailsCorner(value)
        ));
        addRenderableWidget(onOffBuilder(config.biomeEnabled()).create(
            right,
            row(top, 5),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.biome"),
            (button, enabled) -> config.setBiomeEnabled(enabled)
        ));
        addRenderableWidget(onOffBuilder(config.targetBlockEnabled()).create(
            right,
            row(top, 6),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.target_block"),
            (button, enabled) -> config.setTargetBlockEnabled(enabled)
        ));
        addRenderableWidget(onOffBuilder(config.targetFluidEnabled()).create(
            right,
            row(top, 7),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.target_fluid"),
            (button, enabled) -> config.setTargetFluidEnabled(enabled)
        ));
        addRenderableWidget(onOffBuilder(config.targetEntityEnabled()).create(
            right,
            row(top, 8),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.target_entity"),
            (button, enabled) -> config.setTargetEntityEnabled(enabled)
        ));
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.detailsHudScale()
        ).withValues(HudScale.values()).create(
            right,
            row(top, 9),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.details_size"),
            (button, value) -> config.setDetailsHudScale(value)
        ));
        addRenderableWidget(CycleButton.builder(
            value -> settingValue(value.displayName()),
            config.detailsBackgroundOpacity()
        ).withValues(BackgroundOpacity.values()).create(
            right,
            row(top, 10),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.details_background"),
            (button, value) -> {
                config.setDetailsBackgroundOpacity(value);
                panelShadowButton.active = hasVisibleBackground(config);
            }
        ));

        addRenderableWidget(Button.builder(Component.translatable("controls.reset"), button -> {
            config.reset();
            rebuildWidgets();
        }).bounds(left, row(top, FOOTER_ROW) + 4, buttonWidth, BUTTON_HEIGHT).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(right, row(top, FOOTER_ROW) + 4, buttonWidth, BUTTON_HEIGHT)
            .build());
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    private void addSectionHeading(int x, int y, int width, String translationKey) {
        addRenderableWidget(new StringWidget(
            x,
            y,
            width,
            BUTTON_HEIGHT,
            Component.translatable(translationKey).withColor(SECTION_COLOR),
            this.font
        ));
    }

    private static CycleButton.Builder<Boolean> onOffBuilder(boolean initialValue) {
        return CycleButton.<Boolean>builder(
            enabled -> Component.translatable(enabled ? "options.on" : "options.off")
                .withColor(enabled ? ENABLED_COLOR : DISABLED_COLOR),
            initialValue
        ).withValues(Boolean.TRUE, Boolean.FALSE);
    }

    private static Component optionName(String translationKey) {
        return Component.translatable(translationKey).withColor(KEY_COLOR);
    }

    private static Component settingValue(String value) {
        return Component.literal(value).withColor(VALUE_COLOR);
    }

    private static boolean hasVisibleBackground(LocatorHudConfig config) {
        return (config.mainPanelEnabled() && config.backgroundOpacity().drawsPanel())
            || (config.detailsPanelEnabled() && config.detailsBackgroundOpacity().drawsPanel());
    }

    private static int row(int top, int index) {
        return top + ROW_SPACING * index;
    }
}
