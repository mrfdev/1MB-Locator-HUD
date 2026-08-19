package dev.mrfdev.locatorhud;

import com.mojang.blaze3d.platform.InputConstants;
import dev.mrfdev.locatorhud.config.LocatorHudConfig;
import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
        ClientHudSampler sampler = new ClientHudSampler(this.config);
        LocatorHudRenderer renderer = new LocatorHudRenderer(this.config, sampler);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> sampler.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> sampler.reset());

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
        KeyMapping copyCoordinatesKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.locatorhud.copy_coordinates",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F8,
            category
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                this.config.toggleEnabled();
                client.gui.hud.setOverlayMessage(
                    Component.translatable(
                        this.config.enabled()
                            ? "message.locatorhud.enabled"
                            : "message.locatorhud.disabled"
                    ),
                    false
                );
            }
            while (copyCoordinatesKey.consumeClick()) {
                copyCoordinates(client);
            }
            sampler.tick(client);
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

    private void copyCoordinates(Minecraft client) {
        if (client.player == null || client.level == null) {
            client.gui.hud.setOverlayMessage(
                Component.translatable("message.locatorhud.coordinates_unavailable"),
                false
            );
            return;
        }

        Identifier dimension = client.level.dimension().identifier();
        CoordinateCopyFormat format = this.config.coordinateCopyFormat();
        Optional<String> copyText = CoordinateCopyFormatter.format(
            format,
            this.config.precision(),
            client.player.getX(),
            client.player.getY(),
            client.player.getZ(),
            client.player.getGameProfile().name(),
            WorldNameFormatter.fromIdentifier(dimension.getNamespace(), dimension.getPath()),
            dimension.getPath()
        );
        if (copyText.isEmpty()) {
            client.gui.hud.setOverlayMessage(
                Component.translatable("message.locatorhud.coordinates_unavailable"),
                false
            );
            return;
        }

        client.keyboardHandler.setClipboard(copyText.orElseThrow());
        client.gui.hud.setOverlayMessage(
            Component.translatable(
                "message.locatorhud.coordinates_copied",
                Component.translatable(format.translationKey())
            ),
            false
        );
    }
}
