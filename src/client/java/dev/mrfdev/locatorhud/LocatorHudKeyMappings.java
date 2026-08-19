package dev.mrfdev.locatorhud;

import com.mojang.blaze3d.platform.InputConstants;
import dev.mrfdev.locatorhud.config.LocatorHudConfig;
import dev.mrfdev.locatorhud.config.LocatorHudConfigScreen;
import java.util.Optional;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

final class LocatorHudKeyMappings {
    private static final String TOGGLE_KEY = "key.locatorhud.toggle";
    private static final String COPY_COORDINATES_KEY = "key.locatorhud.copy_coordinates";
    private static final String OPEN_SETTINGS_KEY = "key.locatorhud.open_settings";

    private final KeyMapping toggle;
    private final KeyMapping copyCoordinates;
    private final KeyMapping openSettings;

    private LocatorHudKeyMappings(
        KeyMapping toggle,
        KeyMapping copyCoordinates,
        KeyMapping openSettings
    ) {
        this.toggle = toggle;
        this.copyCoordinates = copyCoordinates;
        this.openSettings = openSettings;
    }

    static LocatorHudKeyMappings register() {
        KeyMapping.Category category = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(LocatorHudClient.MOD_ID, "controls")
        );
        return new LocatorHudKeyMappings(
            register(TOGGLE_KEY, InputConstants.KEY_F7, category),
            register(COPY_COORDINATES_KEY, InputConstants.KEY_F8, category),
            register(OPEN_SETTINGS_KEY, InputConstants.UNKNOWN.getValue(), category)
        );
    }

    private static KeyMapping register(
        String translationKey,
        int defaultKey,
        KeyMapping.Category category
    ) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
            translationKey,
            InputConstants.Type.KEYSYM,
            defaultKey,
            category
        ));
    }

    void handle(Minecraft client, LocatorHudConfig config) {
        while (this.toggle.consumeClick()) {
            toggleHud(client, config);
        }
        while (this.copyCoordinates.consumeClick()) {
            copyCoordinates(client, config);
        }
        while (this.openSettings.consumeClick()) {
            openSettings(client);
        }
    }

    private static void toggleHud(Minecraft client, LocatorHudConfig config) {
        config.toggleEnabled();
        client.gui.hud.setOverlayMessage(
            Component.translatable(
                config.enabled()
                    ? "message.locatorhud.enabled"
                    : "message.locatorhud.disabled"
            ),
            false
        );
    }

    private static void copyCoordinates(Minecraft client, LocatorHudConfig config) {
        if (client.player == null || client.level == null) {
            client.gui.hud.setOverlayMessage(
                Component.translatable("message.locatorhud.coordinates_unavailable"),
                false
            );
            return;
        }
        if (!DebugInfoPolicy.allowsCoordinates(client.player.isReducedDebugInfo())) {
            client.gui.hud.setOverlayMessage(
                Component.translatable("message.locatorhud.coordinates_restricted"),
                false
            );
            return;
        }

        Identifier dimension = client.level.dimension().identifier();
        CoordinateCopyFormat format = config.coordinateCopyFormat();
        Optional<String> copyText = CoordinateCopyFormatter.format(
            format,
            config.precision(),
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

    static void openSettings(Minecraft client) {
        Screen currentScreen = client.gui.screen();
        if (currentScreen instanceof LocatorHudConfigScreen) {
            return;
        }
        client.gui.setScreen(new LocatorHudConfigScreen(currentScreen));
    }
}
