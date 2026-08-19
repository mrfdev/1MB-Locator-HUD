package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.ConfigScreenLayout;
import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.DiscreteSliderOptions;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.LocatorHudClient;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewAnglePrecision;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class LocatorHudConfigScreen extends Screen {
    private static final int BUTTON_HEIGHT = 20;
    private static final int HEADING_HEIGHT = 16;
    private static final int CONTROL_SPACING = 4;
    private static final int SECTION_SPACING = 8;
    private static final int FOOTER_BUTTON_WIDTH = 150;
    private static final int FOOTER_GAP = 4;
    private static final int KEY_COLOR = 0xD8D8D8;
    private static final int VALUE_COLOR = 0xFFFFFF;
    private static final int ENABLED_COLOR = 0x55FF55;
    private static final int DISABLED_COLOR = 0xFF7777;
    private static final int SECTION_COLOR = 0xFFD166;
    private static final Duration TOOLTIP_DELAY = Duration.ofSeconds(1);
    private static final DiscreteSliderOptions<HudScale> HUD_SCALE_OPTIONS =
        new DiscreteSliderOptions<>(List.of(HudScale.values()), HudScale::sliderPosition);
    private static final DiscreteSliderOptions<BackgroundOpacity> BACKGROUND_OPTIONS =
        new DiscreteSliderOptions<>(
            List.of(BackgroundOpacity.values()),
            BackgroundOpacity::sliderPosition
        );

    private final Screen parent;
    private LocatorHudPreset selectedPreset = LocatorHudPreset.MINIMAL;
    private LocatorHudConfig config;
    private CycleButton<CoordinatePrecision> precisionButton;
    private CycleButton<ViewAnglePrecision> anglePrecisionButton;
    private CycleButton<Boolean> panelShadowButton;

    public LocatorHudConfigScreen(Screen parent) {
        super(Component.translatable("screen.locatorhud.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.config = LocatorHudClient.instance().config();
        ConfigScreenLayout.Plan plan = ConfigScreenLayout.forScreenWidth(this.width);
        HeaderAndFooterLayout screenLayout = new HeaderAndFooterLayout(this);
        screenLayout.addTitleHeader(this.title, this.font);

        LinearLayout content = buildContent(this.config, plan);
        ScrollableLayout scrolling = new ScrollableLayout(
            this.minecraft,
            content,
            Math.max(1, screenLayout.getContentHeight()),
            ScrollableLayout.ReserveStrategy.BOTH
        );
        scrolling.setMinWidth(plan.contentWidth());
        screenLayout.addToContents(scrolling);
        screenLayout.addToFooter(buildFooter(this.config));
        screenLayout.arrangeElements();
        screenLayout.visitWidgets(this::addRenderableWidget);
        refreshControlStates();
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    private LinearLayout buildContent(LocatorHudConfig config, ConfigScreenLayout.Plan plan) {
        LinearLayout content = LinearLayout.vertical().spacing(SECTION_SPACING);
        content.defaultCellSetting().alignHorizontallyCenter();
        content.addChild(buildGlobalSection(config, plan));
        content.addChild(buildSetupSection(config, plan));

        if (plan.twoColumns()) {
            LinearLayout panelColumns = LinearLayout.horizontal().spacing(ConfigScreenLayout.COLUMN_GAP);
            panelColumns.defaultCellSetting().alignVerticallyTop();
            panelColumns.addChild(buildMainSection(config, plan.buttonWidth()));
            panelColumns.addChild(buildDetailsSection(config, plan.buttonWidth()));
            content.addChild(panelColumns);
        } else {
            content.addChild(buildMainSection(config, plan.buttonWidth()));
            content.addChild(buildDetailsSection(config, plan.buttonWidth()));
        }
        return content;
    }

    private LinearLayout buildGlobalSection(LocatorHudConfig config, ConfigScreenLayout.Plan plan) {
        int buttonWidth = plan.buttonWidth();
        List<Integer> shadowWidths = ConfigScreenLayout.equalColumnWidths(
            buttonWidth,
            2,
            CONTROL_SPACING
        );
        CycleButton<Boolean> enabledButton = cycleButton(
            tooltipBuilder(onOffBuilder(config.enabled()), "tooltip.locatorhud.enabled"),
            buttonWidth,
            "option.locatorhud.enabled",
            (button, enabled) -> config.setEnabled(enabled)
        );
        CycleButton<ColorPalette> paletteButton = cycleButton(
            tooltipBuilder(
                CycleButton.builder(value -> settingValue(value.displayName()), config.palette()),
                "tooltip.locatorhud.palette"
            ).withValues(ColorPalette.values()),
            buttonWidth - Checkbox.getBoxSize(this.font) - CONTROL_SPACING,
            "option.locatorhud.palette",
            (button, value) -> config.setPalette(value)
        );
        Checkbox biomeThemeOverrideCheckbox = Checkbox.builder(Component.empty(), this.font)
            .maxWidth(Checkbox.getBoxSize(this.font))
            .selected(config.biomeThemeOverrideEnabled())
            .onValueChange((checkbox, enabled) -> config.setBiomeThemeOverrideEnabled(enabled))
            .build();
        biomeThemeOverrideCheckbox.setTooltip(Tooltip.create(
            Component.translatable("tooltip.locatorhud.biome_theme_override"),
            Component.translatable("narration.locatorhud.biome_theme_override")
        ));
        biomeThemeOverrideCheckbox.setTooltipDelay(TOOLTIP_DELAY);
        LinearLayout paletteRow = controlRow(paletteButton, biomeThemeOverrideCheckbox);
        CycleButton<Boolean> textShadowButton = cycleButton(
            tooltipBuilder(onOffBuilder(config.textShadow()), "tooltip.locatorhud.text_shadow"),
            shadowWidths.get(0),
            "option.locatorhud.text_shadow",
            (button, enabled) -> config.setTextShadow(enabled)
        );
        this.panelShadowButton = cycleButton(
            tooltipBuilder(onOffBuilder(config.panelShadow()), "tooltip.locatorhud.panel_shadow"),
            shadowWidths.get(1),
            "option.locatorhud.panel_shadow",
            (button, enabled) -> config.setPanelShadow(enabled)
        );
        LinearLayout shadowRow = controlRow(textShadowButton, this.panelShadowButton);
        CycleButton<CoordinateCopyFormat> coordinateCopyButton = cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    LocatorHudConfigScreen::coordinateCopyFormatValue,
                    config.coordinateCopyFormat()
                ),
                "tooltip.locatorhud.coordinate_copy_format"
            ).withValues(CoordinateCopyFormat.values()),
            buttonWidth,
            "option.locatorhud.coordinate_copy_format",
            (button, value) -> config.setCoordinateCopyFormat(value)
        );

        LinearLayout section = section("section.locatorhud.general", plan.contentWidth());
        if (plan.twoColumns()) {
            GridLayout controls = new GridLayout().columnSpacing(ConfigScreenLayout.COLUMN_GAP)
                .rowSpacing(CONTROL_SPACING);
            GridLayout.RowHelper rows = controls.createRowHelper(2);
            rows.addChild(enabledButton);
            rows.addChild(paletteRow);
            rows.addChild(shadowRow);
            rows.addChild(coordinateCopyButton);
            section.addChild(controls);
        } else {
            section.addChild(enabledButton);
            section.addChild(paletteRow);
            section.addChild(shadowRow);
            section.addChild(coordinateCopyButton);
        }
        return section;
    }

    private LinearLayout buildSetupSection(LocatorHudConfig config, ConfigScreenLayout.Plan plan) {
        int buttonWidth = plan.buttonWidth();
        List<Integer> controlWidths = ConfigScreenLayout.equalColumnWidths(
            buttonWidth,
            2,
            CONTROL_SPACING
        );
        CycleButton<LocatorHudPreset> presetButton = cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    value -> Component.translatable(value.translationKey()).withColor(VALUE_COLOR),
                    this.selectedPreset
                ).withValues(LocatorHudPreset.values()),
                "tooltip.locatorhud.preset"
            ),
            controlWidths.get(0),
            "option.locatorhud.preset",
            (button, value) -> this.selectedPreset = value
        );
        Button applyPresetButton = withTooltip(
            Button.builder(Component.translatable("button.locatorhud.apply_preset"), button -> {
                config.applyPreset(this.selectedPreset);
                rebuildWidgets();
            }).size(controlWidths.get(1), BUTTON_HEIGHT).build(),
            "tooltip.locatorhud.apply_preset"
        );
        LinearLayout presetRow = controlRow(presetButton, applyPresetButton);

        Button saveSetupButton = withTooltip(
            Button.builder(Component.translatable("button.locatorhud.save_setup"), button -> {
                if (config.hasSavedSetup()) {
                    showConfirmation(
                        "confirm.locatorhud.overwrite_setup.title",
                        "confirm.locatorhud.overwrite_setup.message",
                        "button.locatorhud.overwrite",
                        () -> config.saveCurrentSetup()
                    );
                } else {
                    config.saveCurrentSetup();
                    rebuildWidgets();
                }
            }).size(controlWidths.get(0), BUTTON_HEIGHT).build(),
            "tooltip.locatorhud.save_setup"
        );
        Button restoreSetupButton = withTooltip(
            Button.builder(Component.translatable("button.locatorhud.restore_setup"), button ->
                showConfirmation(
                    "confirm.locatorhud.restore_setup.title",
                    "confirm.locatorhud.restore_setup.message",
                    "button.locatorhud.restore",
                    config::restoreSavedSetup
                )
            ).size(controlWidths.get(1), BUTTON_HEIGHT).build(),
            "tooltip.locatorhud.restore_setup"
        );
        restoreSetupButton.active = config.hasSavedSetup();
        LinearLayout savedSetupRow = controlRow(saveSetupButton, restoreSetupButton);

        LinearLayout section = section("section.locatorhud.setup", plan.contentWidth());
        if (plan.twoColumns()) {
            GridLayout controls = new GridLayout().columnSpacing(ConfigScreenLayout.COLUMN_GAP);
            GridLayout.RowHelper rows = controls.createRowHelper(2);
            rows.addChild(presetRow);
            rows.addChild(savedSetupRow);
            section.addChild(controls);
        } else {
            section.addChild(presetRow);
            section.addChild(savedSetupRow);
        }
        return section;
    }

    private LinearLayout buildMainSection(LocatorHudConfig config, int width) {
        LinearLayout section = section("section.locatorhud.main_panel", width);
        List<Integer> panelWidths = ConfigScreenLayout.equalColumnWidths(width, 2, CONTROL_SPACING);
        section.addChild(controlRow(
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.mainPanelEnabled()),
                    "tooltip.locatorhud.main_panel_enabled"
                ),
                panelWidths.get(0),
                "option.locatorhud.main_panel_enabled_short",
                (button, enabled) -> {
                    config.setMainPanelEnabled(enabled);
                    refreshControlStates();
                }
            ),
            cycleButton(
                tooltipBuilder(
                    CycleButton.builder(LocatorHudConfigScreen::cornerValue, config.corner()),
                    "tooltip.locatorhud.position"
                ).withValues(HudCorner.values()),
                panelWidths.get(1),
                "option.locatorhud.position_short",
                (button, value) -> config.setCorner(value)
            )
        ));
        section.addChild(cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    value -> settingValue(value.displayName()),
                    config.coordinateDisplay()
                ),
                "tooltip.locatorhud.coordinate_display"
            ).withValues(CoordinateDisplayMode.values()),
            width,
            "option.locatorhud.coordinate_display",
            (button, value) -> {
                config.setCoordinateDisplay(value);
                refreshControlStates();
            }
        ));

        this.precisionButton = cycleButton(
            tooltipBuilder(
                CycleButton.builder(value -> settingValue(value.displayName()), config.precision()),
                "tooltip.locatorhud.decimal_precision"
            ).withValues(
                CoordinatePrecision.NONE,
                CoordinatePrecision.ONE_DECIMAL,
                CoordinatePrecision.TWO_DECIMALS
            ),
            width,
            "option.locatorhud.decimal_precision",
            (button, value) -> config.setPrecision(value)
        );
        section.addChild(this.precisionButton);
        section.addChild(cycleButton(
            tooltipBuilder(
                onOffBuilder(config.coordinateLensEnabled()),
                "tooltip.locatorhud.coordinate_lens"
            ),
            width,
            "option.locatorhud.coordinate_lens",
            (button, enabled) -> {
                config.setCoordinateLensEnabled(enabled);
                refreshControlStates();
            }
        ));
        section.addChild(cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    LocatorHudConfigScreen::worldNameDisplayValue,
                    config.worldNameDisplay()
                ),
                "tooltip.locatorhud.world_name"
            ).withValues(WorldNameDisplay.values()),
            width,
            "option.locatorhud.world_name",
            (button, value) -> config.setWorldNameDisplay(value)
        ));
        List<Integer> viewWidths = ConfigScreenLayout.equalColumnWidths(width, 2, CONTROL_SPACING);
        section.addChild(controlRow(
            cycleButton(
                tooltipBuilder(
                    CycleButton.builder(
                        LocatorHudConfigScreen::viewDirectionDisplayValue,
                        config.viewDirectionDisplay()
                    ).withValues(ViewDirectionDisplay.values()),
                    "tooltip.locatorhud.view_direction"
                ),
                viewWidths.get(0),
                "option.locatorhud.view_direction_short",
                (button, value) -> config.setViewDirectionDisplay(value)
            ),
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.viewAnglesEnabled()),
                    "tooltip.locatorhud.view_angles"
                ),
                viewWidths.get(1),
                "option.locatorhud.view_angles_short",
                (button, enabled) -> {
                    config.setViewAnglesEnabled(enabled);
                    refreshControlStates();
                }
            )
        ));

        this.anglePrecisionButton = cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    LocatorHudConfigScreen::viewAnglePrecisionValue,
                    config.viewAnglePrecision()
                ),
                "tooltip.locatorhud.angle_decimals"
            ).withValues(ViewAnglePrecision.values()),
            width,
            "option.locatorhud.angle_decimals",
            (button, value) -> config.setViewAnglePrecision(value)
        );
        section.addChild(this.anglePrecisionButton);
        addSizeAndBackgroundControls(
            section,
            width,
            "option.locatorhud.hud_size",
            "tooltip.locatorhud.hud_size",
            config.hudScale(),
            config::setHudScale,
            "option.locatorhud.background",
            "tooltip.locatorhud.background",
            config.backgroundOpacity(),
            value -> {
                config.setBackgroundOpacity(value);
                refreshControlStates();
            }
        );
        return section;
    }

    private LinearLayout buildDetailsSection(LocatorHudConfig config, int width) {
        LinearLayout section = section("section.locatorhud.details_panel", width);
        List<Integer> panelWidths = ConfigScreenLayout.equalColumnWidths(width, 2, CONTROL_SPACING);
        section.addChild(controlRow(
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.detailsPanelEnabled()),
                    "tooltip.locatorhud.details_panel_enabled"
                ),
                panelWidths.get(0),
                "option.locatorhud.details_panel_enabled_short",
                (button, enabled) -> {
                    config.setDetailsPanelEnabled(enabled);
                    refreshControlStates();
                }
            ),
            cycleButton(
                tooltipBuilder(
                    CycleButton.builder(
                        LocatorHudConfigScreen::cornerValue,
                        config.detailsCorner()
                    ),
                    "tooltip.locatorhud.details_position"
                ).withValues(HudCorner.values()),
                panelWidths.get(1),
                "option.locatorhud.position_short",
                (button, value) -> config.setDetailsCorner(value)
            )
        ));
        List<Integer> detailValueWidths = ConfigScreenLayout.equalColumnWidths(
            width,
            3,
            CONTROL_SPACING
        );
        section.addChild(controlRow(
            cycleButton(
                tooltipBuilder(onOffBuilder(config.biomeEnabled()), "tooltip.locatorhud.biome"),
                detailValueWidths.get(0),
                "option.locatorhud.biome",
                (button, enabled) -> config.setBiomeEnabled(enabled)
            ),
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.biomeTransitionEnabled()),
                    "tooltip.locatorhud.biome_transition"
                ),
                detailValueWidths.get(1),
                "option.locatorhud.biome_transition_short",
                (button, enabled) -> config.setBiomeTransitionEnabled(enabled)
            ),
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.movementSpeedEnabled()),
                    "tooltip.locatorhud.movement_speed"
                ),
                detailValueWidths.get(2),
                "option.locatorhud.movement_speed_short",
                (button, enabled) -> config.setMovementSpeedEnabled(enabled)
            )
        ));
        List<Integer> targetWidths = ConfigScreenLayout.equalColumnWidths(width, 3, CONTROL_SPACING);
        section.addChild(controlRow(
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.targetBlockEnabled()),
                    "tooltip.locatorhud.target_block"
                ),
                targetWidths.get(0),
                "option.locatorhud.target_block_short",
                (button, enabled) -> config.setTargetBlockEnabled(enabled)
            ),
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.targetFluidEnabled()),
                    "tooltip.locatorhud.target_fluid"
                ),
                targetWidths.get(1),
                "option.locatorhud.target_fluid_short",
                (button, enabled) -> config.setTargetFluidEnabled(enabled)
            ),
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.targetEntityEnabled()),
                    "tooltip.locatorhud.target_entity"
                ),
                targetWidths.get(2),
                "option.locatorhud.target_entity_short",
                (button, enabled) -> config.setTargetEntityEnabled(enabled)
            )
        ));
        section.addChild(cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    LocatorHudConfigScreen::targetNameModeValue,
                    config.targetNameMode()
                ),
                "tooltip.locatorhud.target_name_mode"
            ).withValues(TargetNameMode.FRIENDLY, TargetNameMode.API_ACCURATE),
            width,
            "option.locatorhud.target_name_mode",
            (button, value) -> config.setTargetNameMode(value)
        ));
        List<Integer> targetBehaviorWidths = ConfigScreenLayout.equalColumnWidths(
            width,
            2,
            CONTROL_SPACING
        );
        section.addChild(controlRow(
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.autoHideEmptyTargetValues()),
                    "tooltip.locatorhud.auto_hide_empty_values"
                ),
                targetBehaviorWidths.get(0),
                "option.locatorhud.auto_hide_empty_values_short",
                (button, enabled) -> config.setAutoHideEmptyTargetValues(enabled)
            ),
            cycleButton(
                tooltipBuilder(
                    onOffBuilder(config.targetLingerEnabled()),
                    "tooltip.locatorhud.target_linger"
                ),
                targetBehaviorWidths.get(1),
                "option.locatorhud.target_linger_short",
                (button, enabled) -> config.setTargetLingerEnabled(enabled)
            )
        ));
        addSizeAndBackgroundControls(
            section,
            width,
            "option.locatorhud.details_size",
            "tooltip.locatorhud.details_size",
            config.detailsHudScale(),
            config::setDetailsHudScale,
            "option.locatorhud.details_background",
            "tooltip.locatorhud.details_background",
            config.detailsBackgroundOpacity(),
            value -> {
                config.setDetailsBackgroundOpacity(value);
                refreshControlStates();
            }
        );
        return section;
    }

    private void addSizeAndBackgroundControls(
        LinearLayout section,
        int width,
        String sizeOptionKey,
        String sizeTooltipKey,
        HudScale scale,
        Consumer<HudScale> onScaleChanged,
        String backgroundOptionKey,
        String backgroundTooltipKey,
        BackgroundOpacity background,
        Consumer<BackgroundOpacity> onBackgroundChanged
    ) {
        if (ConfigScreenLayout.pairsPanelSliders(width)) {
            List<Integer> sliderWidths = ConfigScreenLayout.equalColumnWidths(
                width,
                2,
                CONTROL_SPACING
            );
            section.addChild(controlRow(
                discreteSlider(
                    sliderWidths.get(0),
                    "option.locatorhud.size_short",
                    sizeTooltipKey,
                    HUD_SCALE_OPTIONS,
                    scale,
                    value -> settingValue(value.displayName()),
                    onScaleChanged
                ),
                discreteSlider(
                    sliderWidths.get(1),
                    "option.locatorhud.background_short",
                    backgroundTooltipKey,
                    BACKGROUND_OPTIONS,
                    background,
                    LocatorHudConfigScreen::backgroundOpacityValue,
                    onBackgroundChanged
                )
            ));
            return;
        }

        section.addChild(discreteSlider(
            width,
            sizeOptionKey,
            sizeTooltipKey,
            HUD_SCALE_OPTIONS,
            scale,
            value -> settingValue(value.displayName()),
            onScaleChanged
        ));
        section.addChild(discreteSlider(
            width,
            backgroundOptionKey,
            backgroundTooltipKey,
            BACKGROUND_OPTIONS,
            background,
            LocatorHudConfigScreen::backgroundOpacityValue,
            onBackgroundChanged
        ));
    }

    private LinearLayout buildFooter(LocatorHudConfig config) {
        int availableWidth = Math.max(2, this.width - ConfigScreenLayout.OUTER_MARGIN - FOOTER_GAP);
        int buttonWidth = Math.max(1, Math.min(FOOTER_BUTTON_WIDTH, availableWidth / 2));
        LinearLayout footer = LinearLayout.horizontal().spacing(FOOTER_GAP);
        footer.addChild(withTooltip(
            Button.builder(Component.translatable("controls.reset"), button -> showConfirmation(
                "confirm.locatorhud.reset.title",
                "confirm.locatorhud.reset.message",
                "button.locatorhud.reset",
                config::reset
            )).size(buttonWidth, BUTTON_HEIGHT).build(),
            "tooltip.locatorhud.reset"
        ));
        footer.addChild(withTooltip(
            Button.builder(Component.translatable("gui.done"), button -> onClose())
                .size(buttonWidth, BUTTON_HEIGHT)
                .build(),
            "tooltip.locatorhud.done"
        ));
        return footer;
    }

    private void showConfirmation(
        String titleKey,
        String messageKey,
        String confirmButtonKey,
        Runnable confirmedAction
    ) {
        this.minecraft.gui.setScreen(new ConfirmScreen(
            confirmed -> {
                if (confirmed) {
                    confirmedAction.run();
                }
                this.minecraft.gui.setScreen(this);
            },
            Component.translatable(titleKey),
            Component.translatable(messageKey),
            Component.translatable(confirmButtonKey),
            CommonComponents.GUI_CANCEL
        ));
    }

    private LinearLayout section(String translationKey, int width) {
        LinearLayout section = LinearLayout.vertical().spacing(CONTROL_SPACING);
        section.defaultCellSetting().alignHorizontallyCenter();
        section.addChild(new StringWidget(
            0,
            0,
            width,
            HEADING_HEIGHT,
            Component.translatable(translationKey).withColor(SECTION_COLOR),
            this.font
        ));
        return section;
    }

    private static LinearLayout controlRow(AbstractWidget... controls) {
        LinearLayout row = LinearLayout.horizontal().spacing(CONTROL_SPACING);
        for (AbstractWidget control : controls) {
            row.addChild(control);
        }
        return row;
    }

    private <T> CycleButton<T> cycleButton(
        CycleButton.Builder<T> builder,
        int width,
        String optionKey,
        CycleButton.OnValueChange<T> onValueChange
    ) {
        CycleButton<T> button = builder.create(
            0,
            0,
            width,
            BUTTON_HEIGHT,
            optionName(optionKey),
            onValueChange
        );
        button.setTooltipDelay(TOOLTIP_DELAY);
        return button;
    }

    private <T> DiscreteOptionSlider<T> discreteSlider(
        int width,
        String optionKey,
        String tooltipKey,
        DiscreteSliderOptions<T> options,
        T initialValue,
        Function<? super T, Component> valueFormatter,
        Consumer<T> onValueChanged
    ) {
        return withTooltip(new DiscreteOptionSlider<>(
            0,
            0,
            width,
            BUTTON_HEIGHT,
            optionName(optionKey),
            options,
            initialValue,
            valueFormatter,
            onValueChanged
        ), tooltipKey);
    }

    private <T extends AbstractWidget> T withTooltip(T widget, String translationKey) {
        widget.setTooltip(tooltip(translationKey));
        widget.setTooltipDelay(TOOLTIP_DELAY);
        return widget;
    }

    private void refreshControlStates() {
        if (this.config == null) {
            return;
        }
        if (this.precisionButton != null) {
            this.precisionButton.active = this.config.coordinateDisplay().showsDecimal()
                || this.config.coordinateLensEnabled();
        }
        if (this.anglePrecisionButton != null) {
            this.anglePrecisionButton.active = this.config.viewAnglesEnabled();
        }
        if (this.panelShadowButton != null) {
            this.panelShadowButton.active = hasVisibleBackground(this.config);
        }
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

    private static Component cornerValue(HudCorner value) {
        String translationKey = switch (value) {
            case TOP_LEFT -> "value.locatorhud.corner.top_left";
            case TOP_RIGHT -> "value.locatorhud.corner.top_right";
            case BOTTOM_LEFT -> "value.locatorhud.corner.bottom_left";
            case BOTTOM_RIGHT -> "value.locatorhud.corner.bottom_right";
        };
        return Component.translatable(translationKey).withColor(VALUE_COLOR);
    }

    private static Component targetNameModeValue(TargetNameMode value) {
        return Component.translatable(value.translationKey()).withColor(VALUE_COLOR);
    }

    private static Component coordinateCopyFormatValue(CoordinateCopyFormat value) {
        return Component.translatable(value.translationKey()).withColor(VALUE_COLOR);
    }

    private static Component worldNameDisplayValue(WorldNameDisplay value) {
        return switch (value) {
            case IN_FRONT -> stateValue(true, " (in front)");
            case BEHIND -> stateValue(true, " (behind)");
            case OFF -> stateValue(false);
        };
    }

    private static Component viewDirectionDisplayValue(ViewDirectionDisplay value) {
        return switch (value) {
            case ON -> stateValue(true);
            case WITH_DETAILS -> Component.empty()
                .append(stateValue(true))
                .append(Component.translatable(
                    "value.locatorhud.view_direction.with_details_suffix"
                ).withColor(VALUE_COLOR));
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

    private static final class DiscreteOptionSlider<T> extends AbstractSliderButton {
        private final Component option;
        private final DiscreteSliderOptions<T> options;
        private final Function<? super T, Component> valueFormatter;
        private final Consumer<T> onValueChanged;
        private T selected;

        private DiscreteOptionSlider(
            int x,
            int y,
            int width,
            int height,
            Component option,
            DiscreteSliderOptions<T> options,
            T initialValue,
            Function<? super T, Component> valueFormatter,
            Consumer<T> onValueChanged
        ) {
            super(x, y, width, height, CommonComponents.EMPTY, options.position(initialValue));
            this.option = Objects.requireNonNull(option, "option");
            this.options = Objects.requireNonNull(options, "options");
            this.selected = Objects.requireNonNull(initialValue, "initialValue");
            this.valueFormatter = Objects.requireNonNull(valueFormatter, "valueFormatter");
            this.onValueChanged = Objects.requireNonNull(onValueChanged, "onValueChanged");
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(CommonComponents.optionNameValue(
                this.option,
                this.valueFormatter.apply(this.selected)
            ));
        }

        @Override
        protected void applyValue() {
            T next = this.options.nearest(this.value);
            this.value = this.options.position(next);
            if (!next.equals(this.selected)) {
                this.selected = next;
                this.onValueChanged.accept(next);
            }
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (this.canChangeValue && (event.isLeft() || event.isRight())) {
                T next = this.options.step(this.selected, event.isLeft() ? -1 : 1);
                setValue(this.options.position(next));
                return true;
            }
            return super.keyPressed(event);
        }
    }
}
