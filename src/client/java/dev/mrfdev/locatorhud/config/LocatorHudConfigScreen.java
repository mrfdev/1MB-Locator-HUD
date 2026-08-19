package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.ConfigScreenLayout;
import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.DiscreteSliderOptions;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.LocatorHudClient;
import dev.mrfdev.locatorhud.PanelGeometry;
import dev.mrfdev.locatorhud.PanelWidth;
import dev.mrfdev.locatorhud.PanelWidthLimits;
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
import net.minecraft.client.input.MouseButtonEvent;
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
    private static final DiscreteSliderOptions<HudScale> STANDARD_HUD_SCALE_OPTIONS =
        DiscreteSliderOptions.evenlySpaced(HudScale.choices(false));
    private static final DiscreteSliderOptions<HudScale> ACCESSIBILITY_HUD_SCALE_OPTIONS =
        DiscreteSliderOptions.evenlySpaced(HudScale.choices(true));
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
    private PanelWidthControls mainWidthControls;
    private PanelWidthControls detailsWidthControls;

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
        this.config.flushPendingSave();
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    protected Component getUsageNarration() {
        if (this.config != null && this.config.accessibilitySettingsEnabled()) {
            return Component.translatable("narration.locatorhud.config_usage");
        }
        return super.getUsageNarration();
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
            shadowWidths.get(0),
            "option.locatorhud.enabled",
            (button, enabled) -> config.setEnabled(enabled)
        );
        CycleButton<Boolean> accessibilityButton = cycleButton(
            tooltipBuilder(
                onOffBuilder(config.accessibilitySettingsEnabled()),
                "tooltip.locatorhud.accessibility_settings"
            ),
            shadowWidths.get(1),
            "option.locatorhud.accessibility_settings",
            (button, enabled) -> {
                config.setAccessibilitySettingsEnabled(enabled);
                rebuildWidgets();
            }
        );
        LinearLayout enabledRow = controlRow(enabledButton, accessibilityButton);
        CycleButton<ColorPalette> paletteButton = cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    value -> translatedValue(value.translationKey()),
                    config.palette()
                ),
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
            rows.addChild(enabledRow);
            rows.addChild(paletteRow);
            rows.addChild(shadowRow);
            rows.addChild(coordinateCopyButton);
            section.addChild(controls);
        } else {
            section.addChild(enabledRow);
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
                    value -> translatedValue(value.translationKey()),
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
        Button placementButton = withTooltip(
            Button.builder(
                Component.translatable("button.locatorhud.edit_panel_placement"),
                button -> {
                    config.flushPendingSave();
                    this.minecraft.gui.setScreen(new LocatorHudPanelPlacementScreen(
                        this,
                        config,
                        LocatorHudClient.instance().panelPlacements()
                    ));
                }
            ).size(buttonWidth, BUTTON_HEIGHT).build(),
            "tooltip.locatorhud.edit_panel_placement"
        );

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
        section.addChild(placementButton);
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
                (button, value) -> config.setMainPanelPlacement(
                    value,
                    PanelGeometry.Offset.ZERO
                )
            )
        ));
        section.addChild(cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    value -> translatedValue(value.translationKey()),
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
                CycleButton.builder(
                    value -> translatedValue(value.translationKey()),
                    config.precision()
                ),
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
            hudScaleOptions(config),
            config::setHudScale,
            "option.locatorhud.background",
            "tooltip.locatorhud.background",
            config.backgroundOpacity(),
            value -> {
                config.setBackgroundOpacity(value);
                refreshControlStates();
            }
        );
        this.mainWidthControls = addPanelWidthControls(
            section,
            width,
            config.mainPanelMinimumWidth(),
            config.mainPanelMaximumWidth(),
            value -> {
                config.setMainPanelMinimumWidth(value);
                refreshPanelWidthControls();
            },
            value -> {
                config.setMainPanelMaximumWidth(value);
                refreshPanelWidthControls();
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
                (button, value) -> config.setDetailsPanelPlacement(
                    value,
                    PanelGeometry.Offset.ZERO
                )
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
            hudScaleOptions(config),
            config::setDetailsHudScale,
            "option.locatorhud.details_background",
            "tooltip.locatorhud.details_background",
            config.detailsBackgroundOpacity(),
            value -> {
                config.setDetailsBackgroundOpacity(value);
                refreshControlStates();
            }
        );
        this.detailsWidthControls = addPanelWidthControls(
            section,
            width,
            config.detailsPanelMinimumWidth(),
            config.detailsPanelMaximumWidth(),
            value -> {
                config.setDetailsPanelMinimumWidth(value);
                refreshPanelWidthControls();
            },
            value -> {
                config.setDetailsPanelMaximumWidth(value);
                refreshPanelWidthControls();
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
        DiscreteSliderOptions<HudScale> scaleOptions,
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
                    scaleOptions,
                    scale,
                    value -> translatedValue(value.translationKey(), value.percentage()),
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
            scaleOptions,
            scale,
            value -> translatedValue(value.translationKey(), value.percentage()),
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

    private CycleButton<PanelWidth> panelWidthButton(
        int width,
        String optionKey,
        String tooltipKey,
        PanelWidth initialValue,
        Consumer<PanelWidth> onValueChanged
    ) {
        return cycleButton(
            tooltipBuilder(
                CycleButton.builder(
                    LocatorHudConfigScreen::panelWidthValue,
                    initialValue
                ).withValues(PanelWidth.values()),
                tooltipKey
            ),
            width,
            optionKey,
            (button, value) -> onValueChanged.accept(value)
        );
    }

    private PanelWidthControls addPanelWidthControls(
        LinearLayout section,
        int width,
        PanelWidth minimum,
        PanelWidth maximum,
        Consumer<PanelWidth> onMinimumChanged,
        Consumer<PanelWidth> onMaximumChanged
    ) {
        List<Integer> controlWidths = ConfigScreenLayout.equalColumnWidths(
            width,
            2,
            CONTROL_SPACING
        );
        PanelWidthControls controls = new PanelWidthControls(
            panelWidthButton(
                controlWidths.get(0),
                "option.locatorhud.minimum_width",
                "tooltip.locatorhud.minimum_width",
                minimum,
                onMinimumChanged
            ),
            panelWidthButton(
                controlWidths.get(1),
                "option.locatorhud.maximum_width",
                "tooltip.locatorhud.maximum_width",
                maximum,
                onMaximumChanged
            )
        );
        section.addChild(controlRow(controls.minimum(), controls.maximum()));
        return controls;
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
            onValueChanged,
            this.config::flushPendingSave
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
            boolean active = this.config.coordinateDisplay().showsDecimal()
                || this.config.coordinateLensEnabled();
            this.precisionButton.active = active;
            refreshDependentTooltip(
                this.precisionButton,
                active,
                "tooltip.locatorhud.decimal_precision",
                "tooltip.locatorhud.decimal_precision_disabled"
            );
        }
        if (this.anglePrecisionButton != null) {
            boolean active = this.config.viewAnglesEnabled();
            this.anglePrecisionButton.active = active;
            refreshDependentTooltip(
                this.anglePrecisionButton,
                active,
                "tooltip.locatorhud.angle_decimals",
                "tooltip.locatorhud.angle_decimals_disabled"
            );
        }
        if (this.panelShadowButton != null) {
            boolean active = hasVisibleBackground(this.config);
            this.panelShadowButton.active = active;
            refreshDependentTooltip(
                this.panelShadowButton,
                active,
                "tooltip.locatorhud.panel_shadow",
                "tooltip.locatorhud.panel_shadow_disabled"
            );
        }
        refreshPanelWidthControls();
    }

    private void refreshPanelWidthControls() {
        if (this.config == null) {
            return;
        }
        refreshPanelWidthControls(this.mainWidthControls, this.config.mainPanelWidthLimits());
        refreshPanelWidthControls(this.detailsWidthControls, this.config.detailsPanelWidthLimits());
    }

    private static void refreshPanelWidthControls(
        PanelWidthControls controls,
        PanelWidthLimits limits
    ) {
        if (controls != null) {
            controls.minimum().setValue(limits.minimum());
            controls.maximum().setValue(limits.maximum());
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

    private void refreshDependentTooltip(
        AbstractWidget widget,
        boolean active,
        String standardTooltipKey,
        String disabledTooltipKey
    ) {
        String tooltipKey = this.config.accessibilitySettingsEnabled() && !active
            ? disabledTooltipKey
            : standardTooltipKey;
        widget.setTooltip(tooltip(tooltipKey));
    }

    private static DiscreteSliderOptions<HudScale> hudScaleOptions(LocatorHudConfig config) {
        return config.accessibilitySettingsEnabled()
            ? ACCESSIBILITY_HUD_SCALE_OPTIONS
            : STANDARD_HUD_SCALE_OPTIONS;
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

    private static Component translatedValue(String translationKey, Object... arguments) {
        return Component.translatable(translationKey, arguments).withColor(VALUE_COLOR);
    }

    private static Component cornerValue(HudCorner value) {
        return translatedValue(value.translationKey());
    }

    private static Component targetNameModeValue(TargetNameMode value) {
        return translatedValue(value.translationKey());
    }

    private static Component coordinateCopyFormatValue(CoordinateCopyFormat value) {
        return translatedValue(value.translationKey());
    }

    private static Component panelWidthValue(PanelWidth value) {
        return value.automatic()
            ? translatedValue(value.translationKey())
            : translatedValue(value.translationKey(), value.pixels());
    }

    private static Component worldNameDisplayValue(WorldNameDisplay value) {
        return switch (value) {
            case IN_FRONT, BEHIND -> translatedValue(value.translationKey(), stateValue(true));
            case OFF -> stateValue(false);
        };
    }

    private static Component viewDirectionDisplayValue(ViewDirectionDisplay value) {
        return switch (value) {
            case ON -> stateValue(true);
            case WITH_DETAILS -> translatedValue(value.translationKey(), stateValue(true));
            case OFF -> stateValue(false);
        };
    }

    private static Component viewAnglePrecisionValue(ViewAnglePrecision value) {
        return value == ViewAnglePrecision.WHOLE
            ? Component.translatable(value.translationKey()).withColor(DISABLED_COLOR)
            : translatedValue(value.translationKey());
    }

    private static Component backgroundOpacityValue(BackgroundOpacity value) {
        return value == BackgroundOpacity.OFF
            ? translatedValue(value.translationKey(), stateValue(false))
            : translatedValue(value.translationKey(), value.percentage());
    }

    private static Component stateValue(boolean enabled) {
        return Component.translatable(enabled ? "options.on" : "options.off")
            .withColor(enabled ? ENABLED_COLOR : DISABLED_COLOR);
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
        private final Runnable onInteractionComplete;
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
            Consumer<T> onValueChanged,
            Runnable onInteractionComplete
        ) {
            super(x, y, width, height, CommonComponents.EMPTY, options.position(initialValue));
            this.option = Objects.requireNonNull(option, "option");
            this.options = Objects.requireNonNull(options, "options");
            this.selected = Objects.requireNonNull(initialValue, "initialValue");
            this.valueFormatter = Objects.requireNonNull(valueFormatter, "valueFormatter");
            this.onValueChanged = Objects.requireNonNull(onValueChanged, "onValueChanged");
            this.onInteractionComplete = Objects.requireNonNull(
                onInteractionComplete,
                "onInteractionComplete"
            );
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
        public void onRelease(MouseButtonEvent event) {
            super.onRelease(event);
            this.onInteractionComplete.run();
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

    private record PanelWidthControls(
        CycleButton<PanelWidth> minimum,
        CycleButton<PanelWidth> maximum
    ) {
        private PanelWidthControls {
            Objects.requireNonNull(minimum, "minimum");
            Objects.requireNonNull(maximum, "maximum");
        }
    }
}
