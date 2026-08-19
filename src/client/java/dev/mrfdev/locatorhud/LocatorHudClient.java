package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.LocatorHudConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class LocatorHudClient implements ClientModInitializer {
    public static final String MOD_ID = "locatorhud";
    private static LocatorHudClient instance;

    private LocatorHudConfig config;
    private HudPanelPlacements panelPlacements;

    @Override
    public void onInitializeClient() {
        instance = this;
        this.config = LocatorHudConfig.load();
        this.panelPlacements = new HudPanelPlacements();
        ClientHudSampler sampler = new ClientHudSampler(this.config);
        LocatorHudRenderer renderer = new LocatorHudRenderer(
            this.config,
            sampler,
            this.panelPlacements
        );
        LocatorHudKeyMappings keyMappings = LocatorHudKeyMappings.register();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> sampler.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> sampler.reset());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> this.config.flushPendingSave());

        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(MOD_ID, "location_panel"),
            renderer::render
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            keyMappings.handle(client, this.config);
            sampler.tick(client);
            this.config.tickPersistence();
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

    public HudPanelPlacements panelPlacements() {
        return this.panelPlacements;
    }
}
