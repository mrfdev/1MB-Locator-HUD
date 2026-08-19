package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.BackgroundOpacity;
import dev.mrfdev.locatorhud.config.ColorPalette;
import dev.mrfdev.locatorhud.config.HudCorner;
import dev.mrfdev.locatorhud.config.LocatorHudConfigScreen;
import dev.mrfdev.locatorhud.config.LocatorHudPanelPlacementScreen;
import dev.mrfdev.locatorhud.config.LocatorHudPreset;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class LocatorHudClientSmokeTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        FabricLoader loader = FabricLoader.getInstance();
        require(loader.isModLoaded("locatorhud"), "Locator HUD was not loaded");
        require(loader.isModLoaded("fabric-api"), "Fabric API was not loaded");

        boolean expectedModMenu = Boolean.getBoolean("locatorhud.expectModMenu");
        boolean actualModMenu = loader.isModLoaded("modmenu");
        require(
            actualModMenu == expectedModMenu,
            "Expected Mod Menu loaded=" + expectedModMenu + ", found " + actualModMenu
        );

        context.runOnClient(client -> {
            require(client != null, "Minecraft client was unavailable");
            verifyOptionValueTranslations();
            Screen previousScreen = client.gui.screen();
            try {
                KeyMapping settingsKey = KeyMapping.get("key.locatorhud.open_settings");
                require(settingsKey != null, "Open-settings key mapping was not registered");
                require(settingsKey.isUnbound(), "Open-settings key did not default to unbound");

                LocatorHudClient.instance().config().setBiomeThemeOverrideEnabled(false);
                LocatorHudClient.instance().config().setAccessibilitySettingsEnabled(false);
                LocatorHudClient.instance().config().setMainPanelMinimumWidth(PanelWidth.AUTO);
                LocatorHudClient.instance().config().setMainPanelMaximumWidth(PanelWidth.AUTO);
                LocatorHudClient.instance().config().setDetailsPanelMinimumWidth(PanelWidth.AUTO);
                LocatorHudClient.instance().config().setDetailsPanelMaximumWidth(PanelWidth.AUTO);
                LocatorHudKeyMappings.openSettings(client);
                require(
                    client.gui.screen() instanceof LocatorHudConfigScreen,
                    "Open-settings action did not show the configuration screen"
                );
                LocatorHudConfigScreen screen = (LocatorHudConfigScreen) client.gui.screen();
                LocatorHudKeyMappings.openSettings(client);
                require(
                    client.gui.screen() == screen,
                    "Open-settings action nested another configuration screen"
                );

                CycleButton<Boolean> accessibilityButton = findBooleanButton(
                    screen,
                    "option.locatorhud.accessibility_settings"
                );
                require(!accessibilityButton.getValue(), "Accessibility did not default to off");
                cycleForward(accessibilityButton, 1);
                require(
                    LocatorHudClient.instance().config().accessibilitySettingsEnabled(),
                    "Accessibility control did not update the configuration"
                );
                LocatorHudClient.instance().config().setHudScale(HudScale.HUGE);
                accessibilityButton = findBooleanButton(
                    screen,
                    "option.locatorhud.accessibility_settings"
                );
                cycleForward(accessibilityButton, 1);
                require(
                    !LocatorHudClient.instance().config().accessibilitySettingsEnabled(),
                    "Accessibility control did not turn off"
                );
                require(
                    LocatorHudClient.instance().config().hudScale() == HudScale.NORMAL,
                    "Turning off accessibility did not return an oversized panel to 100%"
                );

                Checkbox magicCheckbox = findMagicCheckbox(screen);
                require(!magicCheckbox.selected(), "Biome-aware colors did not default to off");
                magicCheckbox.onPress(null);
                require(
                    LocatorHudClient.instance().config().biomeThemeOverrideEnabled(),
                    "Biome-aware colors checkbox did not update the configuration"
                );

                List<CycleButton<PanelWidth>> widthButtons = findPanelWidthButtons(screen);
                CycleButton<PanelWidth> mainMinimumWidth = widthButtons.get(0);
                CycleButton<PanelWidth> mainMaximumWidth = widthButtons.get(1);
                cycleForward(mainMaximumWidth, 2);
                cycleForward(mainMinimumWidth, 4);
                require(
                    LocatorHudClient.instance().config().mainPanelWidthLimits().equals(
                        new PanelWidthLimits(PanelWidth.PX_240, PanelWidth.PX_240)
                    ),
                    "Crossed main width controls did not repair the companion bound"
                );
                require(
                    mainMinimumWidth.getValue() == PanelWidth.PX_240
                        && mainMaximumWidth.getValue() == PanelWidth.PX_240,
                    "Repaired main width bounds were not reflected by both controls"
                );

                CycleButton<LocatorHudPreset> presetButton = findPresetButton(screen);
                presetButton.mouseScrolled(0.0, 0.0, 0.0, -1.0);
                presetButton.mouseScrolled(0.0, 0.0, 0.0, -1.0);
                require(
                    presetButton.getValue() == LocatorHudPreset.BUILDER,
                    "Two forward preset choices did not select Builder"
                );
                findButton(screen, "button.locatorhud.apply_preset").onPress(null);

                require(
                    findPresetButton(screen).getValue() == LocatorHudPreset.BUILDER,
                    "Applying Builder reset the preset selector instead of preserving Builder"
                );
                require(
                    findMagicCheckbox(screen).selected(),
                    "Applying a preset unexpectedly disabled biome-aware colors"
                );
                List<CycleButton<PanelWidth>> rebuiltWidthButtons = findPanelWidthButtons(screen);
                require(
                    rebuiltWidthButtons.get(0).getValue() == PanelWidth.PX_240
                        && rebuiltWidthButtons.get(1).getValue() == PanelWidth.PX_240,
                    "Applying a preset unexpectedly changed the main panel width limits"
                );

                findButton(screen, "button.locatorhud.edit_panel_placement").onPress(null);
                require(
                    client.gui.screen() instanceof LocatorHudPanelPlacementScreen,
                    "Place panels did not open the direct placement editor"
                );
                ((LocatorHudPanelPlacementScreen) client.gui.screen()).onClose();
                require(
                    client.gui.screen() == screen,
                    "Closing the placement editor did not return to configuration"
                );
            } finally {
                client.gui.setScreen(previousScreen);
            }
        });
    }

    private static Checkbox findMagicCheckbox(Screen screen) {
        for (GuiEventListener child : descendants(screen)) {
            if (child instanceof Checkbox checkbox) {
                return checkbox;
            }
        }
        throw new AssertionError("Biome-aware colors checkbox was unavailable");
    }

    @SuppressWarnings("unchecked")
    private static CycleButton<LocatorHudPreset> findPresetButton(Screen screen) {
        for (GuiEventListener child : descendants(screen)) {
            if (child instanceof CycleButton<?> cycleButton
                && cycleButton.getValue() instanceof LocatorHudPreset) {
                return (CycleButton<LocatorHudPreset>) cycleButton;
            }
        }
        throw new AssertionError("Preset selector was unavailable");
    }

    @SuppressWarnings("unchecked")
    private static CycleButton<Boolean> findBooleanButton(Screen screen, String optionKey) {
        String prefix = Component.translatable(optionKey).getString() + ": ";
        for (GuiEventListener child : descendants(screen)) {
            if (child instanceof CycleButton<?> cycleButton
                && cycleButton.getValue() instanceof Boolean
                && cycleButton.getMessage().getString().startsWith(prefix)) {
                return (CycleButton<Boolean>) cycleButton;
            }
        }
        throw new AssertionError("Boolean setting was unavailable: " + prefix);
    }

    @SuppressWarnings("unchecked")
    private static List<CycleButton<PanelWidth>> findPanelWidthButtons(Screen screen) {
        List<CycleButton<PanelWidth>> buttons = new ArrayList<>();
        for (GuiEventListener child : descendants(screen)) {
            if (child instanceof CycleButton<?> cycleButton
                && cycleButton.getValue() instanceof PanelWidth) {
                buttons.add((CycleButton<PanelWidth>) cycleButton);
            }
        }
        require(buttons.size() == 4, "Expected four panel width controls, found " + buttons.size());
        return List.copyOf(buttons);
    }

    private static void cycleForward(CycleButton<?> button, int steps) {
        for (int step = 0; step < steps; step++) {
            button.mouseScrolled(0.0, 0.0, 0.0, -1.0);
        }
    }

    private static Button findButton(Screen screen, String translationKey) {
        String label = Component.translatable(translationKey).getString();
        for (GuiEventListener child : descendants(screen)) {
            if (child instanceof Button button && button.getMessage().getString().equals(label)) {
                return button;
            }
        }
        throw new AssertionError("Button was unavailable: " + label);
    }

    private static void verifyOptionValueTranslations() {
        for (CoordinateDisplayMode value : CoordinateDisplayMode.values()) {
            requireTranslated(value.translationKey());
        }
        for (CoordinatePrecision value : CoordinatePrecision.values()) {
            requireTranslated(value.translationKey());
        }
        for (HudScale value : HudScale.values()) {
            requireTranslated(value.translationKey(), value.percentage());
        }
        for (WorldNameDisplay value : WorldNameDisplay.values()) {
            if (value.showsWorld()) {
                requireTranslated(value.translationKey(), Component.translatable("options.on"));
            } else {
                requireTranslated(value.translationKey());
            }
        }
        for (ViewDirectionDisplay value : ViewDirectionDisplay.values()) {
            if (value == ViewDirectionDisplay.WITH_DETAILS) {
                requireTranslated(value.translationKey(), Component.translatable("options.on"));
            } else {
                requireTranslated(value.translationKey());
            }
        }
        for (ViewAnglePrecision value : ViewAnglePrecision.values()) {
            requireTranslated(value.translationKey());
        }
        for (BackgroundOpacity value : BackgroundOpacity.values()) {
            if (value == BackgroundOpacity.OFF) {
                requireTranslated(value.translationKey(), Component.translatable("options.off"));
            } else {
                requireTranslated(value.translationKey(), value.percentage());
            }
        }
        for (ColorPalette value : ColorPalette.values()) {
            requireTranslated(value.translationKey());
        }
        for (HudCorner value : HudCorner.values()) {
            requireTranslated(value.translationKey());
        }
        for (TargetNameMode value : TargetNameMode.values()) {
            requireTranslated(value.translationKey());
        }
        for (CoordinateCopyFormat value : CoordinateCopyFormat.values()) {
            requireTranslated(value.translationKey());
        }
        for (PanelWidth value : PanelWidth.values()) {
            if (value.automatic()) {
                requireTranslated(value.translationKey());
            } else {
                requireTranslated(value.translationKey(), value.pixels());
            }
        }
        for (LocatorHudPreset value : LocatorHudPreset.values()) {
            requireTranslated(value.translationKey());
        }
    }

    private static void requireTranslated(String translationKey, Object... arguments) {
        String resolved = Component.translatable(translationKey, arguments).getString();
        require(!resolved.equals(translationKey), "Missing translation: " + translationKey);
        require(
            !resolved.contains("%s") && !resolved.contains("%%"),
            "Unresolved translation placeholder: " + translationKey
        );
    }

    private static List<GuiEventListener> descendants(ContainerEventHandler container) {
        List<GuiEventListener> descendants = new ArrayList<>();
        collectDescendants(container, descendants);
        return List.copyOf(descendants);
    }

    private static void collectDescendants(
        ContainerEventHandler container,
        List<GuiEventListener> descendants
    ) {
        for (GuiEventListener child : container.children()) {
            descendants.add(child);
            if (child instanceof ContainerEventHandler nestedContainer) {
                collectDescendants(nestedContainer, descendants);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
