package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.LocatorHudClient;
import dev.mrfdev.locatorhud.ViewAnglePrecision;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import java.time.Duration;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class LocatorHudConfigScreen extends Screen {
    private static final int MAX_BUTTON_WIDTH = 240;
    private static final int BUTTON_HEIGHT = 16;
    private static final int ROW_SPACING = 16;
    private static final int COLUMN_GAP = 4;
    private static final int FOOTER_ROW = 12;
    private static final int KEY_COLOR = 0xD8D8D8;
    private static final int VALUE_COLOR = 0xFFFFFF;
    private static final int ENABLED_COLOR = 0x55FF55;
    private static final int DISABLED_COLOR = 0xFF7777;
    private static final int SECTION_COLOR = 0xFFD166;
    private static final Duration TOOLTIP_DELAY = Duration.ofSeconds(1);

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

        CycleButton<CoordinatePrecision> precisionButton = tooltipBuilder(
            CycleButton.builder(
                value -> settingValue(value.displayName()),
                config.precision()
            ),
            "tooltip.locatorhud.decimal_precision"
        ).withValues(
            CoordinatePrecision.NONE,
            CoordinatePrecision.ONE_DECIMAL,
            CoordinatePrecision.TWO_DECIMALS
        ).create(
            left,
            row(top, 6),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.decimal_precision"),
            (button, value) -> config.setPrecision(value)
        );
        precisionButton.active = config.coordinateDisplay().showsDecimal();

        CycleButton<ViewAnglePrecision> anglePrecisionButton = tooltipBuilder(
            CycleButton.builder(
                LocatorHudConfigScreen::viewAnglePrecisionValue,
                config.viewAnglePrecision()
            ),
            "tooltip.locatorhud.angle_decimals"
        ).withValues(ViewAnglePrecision.values()).create(
            left,
            row(top, 9),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.angle_decimals"),
            (button, value) -> config.setViewAnglePrecision(value)
        );
        anglePrecisionButton.active = config.viewAnglesEnabled();

        CycleButton<Boolean> panelShadowButton = tooltipBuilder(
            onOffBuilder(config.panelShadow()),
            "tooltip.locatorhud.panel_shadow"
        ).create(
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

        addCycleButton(tooltipBuilder(
            onOffBuilder(config.enabled()),
            "tooltip.locatorhud.enabled"
        ).create(
            left,
            row(top, 0),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.enabled"),
            (button, enabled) -> config.setEnabled(enabled)
        ));
        addCycleButton(tooltipBuilder(
            CycleButton.builder(
                value -> settingValue(value.displayName()),
                config.palette()
            ),
            "tooltip.locatorhud.palette"
        ).withValues(ColorPalette.values()).create(
            right,
            row(top, 0),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.palette"),
            (button, value) -> config.setPalette(value)
        ));
        addCycleButton(tooltipBuilder(
            onOffBuilder(config.textShadow()),
            "tooltip.locatorhud.text_shadow"
        ).create(
            left,
            row(top, 1),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.text_shadow"),
            (button, enabled) -> config.setTextShadow(enabled)
        ));
        addCycleButton(panelShadowButton);

        addSectionHeading(left, row(top, 2), buttonWidth, "section.locatorhud.main_panel");
        addSectionHeading(right, row(top, 2), buttonWidth, "section.locatorhud.details_panel");

        addCycleButton(tooltipBuilder(
            onOffBuilder(config.mainPanelEnabled()),
            "tooltip.locatorhud.main_panel_enabled"
        ).create(
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
        addCycleButton(tooltipBuilder(
            CycleButton.builder(
                value -> settingValue(value.displayName()),
                config.corner()
            ),
            "tooltip.locatorhud.position"
        ).withValues(HudCorner.values()).create(
            left,
            row(top, 4),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.position"),
            (button, value) -> config.setCorner(value)
        ));
        addCycleButton(tooltipBuilder(
            CycleButton.builder(
                value -> settingValue(value.displayName()),
                config.coordinateDisplay()
            ),
            "tooltip.locatorhud.coordinate_display"
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
        addCycleButton(precisionButton);
        addCycleButton(tooltipBuilder(
            CycleButton.builder(
                LocatorHudConfigScreen::worldNameDisplayValue,
                config.worldNameDisplay()
            ),
            "tooltip.locatorhud.world_name"
        ).withValues(WorldNameDisplay.values()).create(
            left,
            row(top, 7),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.world_name"),
            (button, value) -> config.setWorldNameDisplay(value)
        ));
        addCycleButton(tooltipBuilder(
            onOffBuilder(config.viewAnglesEnabled()),
            "tooltip.locatorhud.view_angles"
        ).create(
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
        addCycleButton(anglePrecisionButton);
        addTooltipWidget(new HudScaleSlider(
            left,
            row(top, 10),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.hud_size"),
            config.hudScale(),
            config::setHudScale
        ), "tooltip.locatorhud.hud_size");
        addTooltipWidget(new BackgroundOpacitySlider(
            left,
            row(top, 11),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.background"),
            config.backgroundOpacity(),
            value -> {
                config.setBackgroundOpacity(value);
                panelShadowButton.active = hasVisibleBackground(config);
            }
        ), "tooltip.locatorhud.background");

        addCycleButton(tooltipBuilder(
            onOffBuilder(config.detailsPanelEnabled()),
            "tooltip.locatorhud.details_panel_enabled"
        ).create(
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
        addCycleButton(tooltipBuilder(
            CycleButton.builder(
                value -> settingValue(value.displayName()),
                config.detailsCorner()
            ),
            "tooltip.locatorhud.details_position"
        ).withValues(HudCorner.values()).create(
            right,
            row(top, 4),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.details_position"),
            (button, value) -> config.setDetailsCorner(value)
        ));
        addCycleButton(tooltipBuilder(
            onOffBuilder(config.biomeEnabled()),
            "tooltip.locatorhud.biome"
        ).create(
            right,
            row(top, 5),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.biome"),
            (button, enabled) -> config.setBiomeEnabled(enabled)
        ));
        addCycleButton(tooltipBuilder(
            onOffBuilder(config.targetBlockEnabled()),
            "tooltip.locatorhud.target_block"
        ).create(
            right,
            row(top, 6),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.target_block"),
            (button, enabled) -> config.setTargetBlockEnabled(enabled)
        ));
        addCycleButton(tooltipBuilder(
            onOffBuilder(config.targetFluidEnabled()),
            "tooltip.locatorhud.target_fluid"
        ).create(
            right,
            row(top, 7),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.target_fluid"),
            (button, enabled) -> config.setTargetFluidEnabled(enabled)
        ));
        addCycleButton(tooltipBuilder(
            onOffBuilder(config.targetEntityEnabled()),
            "tooltip.locatorhud.target_entity"
        ).create(
            right,
            row(top, 8),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.target_entity"),
            (button, enabled) -> config.setTargetEntityEnabled(enabled)
        ));
        addCycleButton(tooltipBuilder(
            onOffBuilder(config.autoHideEmptyTargetValues()),
            "tooltip.locatorhud.auto_hide_empty_values"
        ).create(
            right,
            row(top, 9),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.auto_hide_empty_values"),
            (button, enabled) -> config.setAutoHideEmptyTargetValues(enabled)
        ));
        addTooltipWidget(new HudScaleSlider(
            right,
            row(top, 10),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.details_size"),
            config.detailsHudScale(),
            config::setDetailsHudScale
        ), "tooltip.locatorhud.details_size");
        addTooltipWidget(new BackgroundOpacitySlider(
            right,
            row(top, 11),
            buttonWidth,
            BUTTON_HEIGHT,
            optionName("option.locatorhud.details_background"),
            config.detailsBackgroundOpacity(),
            value -> {
                config.setDetailsBackgroundOpacity(value);
                panelShadowButton.active = hasVisibleBackground(config);
            }
        ), "tooltip.locatorhud.details_background");

        addTooltipWidget(Button.builder(Component.translatable("controls.reset"), button -> {
            config.reset();
            rebuildWidgets();
        }).bounds(left, row(top, FOOTER_ROW) + 4, buttonWidth, BUTTON_HEIGHT).build(),
            "tooltip.locatorhud.reset");
        addTooltipWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(right, row(top, FOOTER_ROW) + 4, buttonWidth, BUTTON_HEIGHT)
            .build(), "tooltip.locatorhud.done");
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

    private <T> CycleButton<T> addCycleButton(CycleButton<T> button) {
        button.setTooltipDelay(TOOLTIP_DELAY);
        return addRenderableWidget(button);
    }

    private <T extends AbstractWidget> T addTooltipWidget(T widget, String translationKey) {
        widget.setTooltip(tooltip(translationKey));
        widget.setTooltipDelay(TOOLTIP_DELAY);
        return addRenderableWidget(widget);
    }

    private static <T> CycleButton.Builder<T> tooltipBuilder(
        CycleButton.Builder<T> builder,
        String translationKey
    ) {
        return builder.withTooltip(value -> tooltip(translationKey));
    }

    private static Tooltip tooltip(String translationKey) {
        return Tooltip.create(Component.translatable(translationKey));
    }

    private static CycleButton.Builder<Boolean> onOffBuilder(boolean initialValue) {
        return CycleButton.<Boolean>builder(
            LocatorHudConfigScreen::stateValue,
            initialValue
        ).withValues(Boolean.TRUE, Boolean.FALSE);
    }

    private static Component optionName(String translationKey) {
        return Component.translatable(translationKey).withColor(KEY_COLOR);
    }

    private static Component settingValue(String value) {
        return Component.literal(value).withColor(VALUE_COLOR);
    }

    private static Component worldNameDisplayValue(WorldNameDisplay value) {
        return switch (value) {
            case IN_FRONT -> stateValue(true, " (in front)");
            case BEHIND -> stateValue(true, " (behind)");
            case OFF -> stateValue(false);
        };
    }

    private static Component viewAnglePrecisionValue(ViewAnglePrecision value) {
        return value == ViewAnglePrecision.WHOLE ? stateValue(false) : settingValue(value.displayName());
    }

    private static Component backgroundOpacityValue(BackgroundOpacity value) {
        return value == BackgroundOpacity.OFF
            ? stateValue(false, " (minimal)")
            : settingValue(value.displayName());
    }

    private static Component stateValue(boolean enabled) {
        return stateValue(enabled, "");
    }

    private static Component stateValue(boolean enabled, String suffix) {
        var state = Component.translatable(enabled ? "options.on" : "options.off")
            .withColor(enabled ? ENABLED_COLOR : DISABLED_COLOR);
        return suffix.isEmpty()
            ? state
            : state.append(Component.literal(suffix).withColor(VALUE_COLOR));
    }

    private static boolean hasVisibleBackground(LocatorHudConfig config) {
        return (config.mainPanelEnabled() && config.backgroundOpacity().drawsPanel())
            || (config.detailsPanelEnabled() && config.detailsBackgroundOpacity().drawsPanel());
    }

    private static int row(int top, int index) {
        return top + ROW_SPACING * index;
    }

    private static final class HudScaleSlider extends AbstractSliderButton {
        private final Component option;
        private final Consumer<HudScale> onValueChanged;
        private HudScale selected;

        private HudScaleSlider(
            int x,
            int y,
            int width,
            int height,
            Component option,
            HudScale initialValue,
            Consumer<HudScale> onValueChanged
        ) {
            super(x, y, width, height, CommonComponents.EMPTY, initialValue.sliderPosition());
            this.option = option;
            this.selected = initialValue;
            this.onValueChanged = onValueChanged;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(CommonComponents.optionNameValue(this.option, settingValue(this.selected.displayName())));
        }

        @Override
        protected void applyValue() {
            HudScale next = HudScale.nearestSliderPosition(this.value);
            this.value = next.sliderPosition();
            if (next != this.selected) {
                this.selected = next;
                this.onValueChanged.accept(next);
            }
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (this.canChangeValue && (event.isLeft() || event.isRight())) {
                int direction = event.isLeft() ? -1 : 1;
                HudScale[] values = HudScale.values();
                int index = Math.max(0, Math.min(values.length - 1, this.selected.ordinal() + direction));
                setValue(values[index].sliderPosition());
                return true;
            }
            return super.keyPressed(event);
        }
    }

    private static final class BackgroundOpacitySlider extends AbstractSliderButton {
        private final Component option;
        private final Consumer<BackgroundOpacity> onValueChanged;
        private BackgroundOpacity selected;

        private BackgroundOpacitySlider(
            int x,
            int y,
            int width,
            int height,
            Component option,
            BackgroundOpacity initialValue,
            Consumer<BackgroundOpacity> onValueChanged
        ) {
            super(x, y, width, height, CommonComponents.EMPTY, initialValue.sliderPosition());
            this.option = option;
            this.selected = initialValue;
            this.onValueChanged = onValueChanged;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(CommonComponents.optionNameValue(this.option, backgroundOpacityValue(this.selected)));
        }

        @Override
        protected void applyValue() {
            BackgroundOpacity next = BackgroundOpacity.nearestSliderPosition(this.value);
            this.value = next.sliderPosition();
            if (next != this.selected) {
                this.selected = next;
                this.onValueChanged.accept(next);
            }
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (this.canChangeValue && (event.isLeft() || event.isRight())) {
                int direction = event.isLeft() ? -1 : 1;
                BackgroundOpacity[] values = BackgroundOpacity.values();
                int index = Math.max(0, Math.min(values.length - 1, this.selected.ordinal() + direction));
                setValue(values[index].sliderPosition());
                return true;
            }
            return super.keyPressed(event);
        }
    }
}
