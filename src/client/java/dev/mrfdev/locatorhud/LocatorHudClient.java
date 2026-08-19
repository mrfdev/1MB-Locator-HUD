package dev.mrfdev.locatorhud;

import com.mojang.blaze3d.platform.InputConstants;
import dev.mrfdev.locatorhud.config.LocatorHudConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class LocatorHudClient implements ClientModInitializer {
    public static final String MOD_ID = "locatorhud";
    private static LocatorHudClient instance;

    private LocatorHudConfig config;

    @Override
    public void onInitializeClient() {
        instance = this;
        this.config = LocatorHudConfig.load();
        LocatorHudRenderer renderer = new LocatorHudRenderer(this.config);

        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(MOD_ID, "location_panel"),
            renderer::render
        );

        KeyMapping.Category category = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "controls")
        );
        KeyMapping toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.locatorhud.toggle",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F7,
            category
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                this.config.toggleEnabled();
                client.gui.hud.setOverlayMessage(
                    Component.translatable(this.config.enabled() ? "message.locatorhud.enabled" : "message.locatorhud.disabled"),
                    false
                );
            }
        });
    }

    public static LocatorHudClient instance() {
        if (instance == null) {
            throw new IllegalStateException("Locator HUD has not initialized yet");
        }
        return instance;
    }

    public LocatorHudConfig config() {
        return this.config;
    }
}
