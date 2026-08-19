package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.LocatorHudConfigScreen;
import dev.mrfdev.locatorhud.config.LocatorHudPreset;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.loader.api.FabricLoader;
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
            Screen previousScreen = client.gui.screen();
            try {
                LocatorHudClient.instance().config().setBiomeThemeOverrideEnabled(false);
                LocatorHudConfigScreen screen = new LocatorHudConfigScreen(previousScreen);
                client.gui.setScreen(screen);

                Checkbox magicCheckbox = findMagicCheckbox(screen);
                require(!magicCheckbox.selected(), "Biome-aware colors did not default to off");
                magicCheckbox.onPress(null);
                require(
                    LocatorHudClient.instance().config().biomeThemeOverrideEnabled(),
                    "Biome-aware colors checkbox did not update the configuration"
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

    private static Button findButton(Screen screen, String translationKey) {
        String label = Component.translatable(translationKey).getString();
        for (GuiEventListener child : descendants(screen)) {
            if (child instanceof Button button && button.getMessage().getString().equals(label)) {
                return button;
            }
        }
        throw new AssertionError("Button was unavailable: " + label);
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
