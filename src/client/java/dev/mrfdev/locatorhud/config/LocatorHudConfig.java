package dev.mrfdev.locatorhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.ViewAnglePrecision;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.fabricmc.loader.api.FabricLoader;

public final class LocatorHudConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("locator-hud.json");

    private boolean enabled = true;
    private boolean mainPanelEnabled = true;
    private boolean detailsPanelEnabled = true;
    private HudCorner corner = HudCorner.TOP_LEFT;
    private CoordinateDisplayMode coordinateDisplay = CoordinateDisplayMode.DECIMAL_ONLY;
    private CoordinatePrecision precision = CoordinatePrecision.ONE_DECIMAL;
    // Retained as a serialized mirror so older versions can still read this configuration.
    private boolean worldNameEnabled = true;
    private WorldNameDisplay worldNameDisplay;
    private boolean viewAnglesEnabled = false;
    private ViewAnglePrecision viewAnglePrecision = ViewAnglePrecision.WHOLE;
    private boolean biomeEnabled = false;
    private boolean targetBlockEnabled = false;
    private boolean targetFluidEnabled = false;
    private boolean targetEntityEnabled = false;
    private boolean autoHideEmptyTargetValues = false;
    private HudScale hudScale = HudScale.NORMAL;
    private HudCorner detailsCorner = HudCorner.TOP_RIGHT;
    private HudScale detailsHudScale = HudScale.COMPACT;
    private ColorPalette palette = ColorPalette.OCEAN;
    private BackgroundOpacity backgroundOpacity = BackgroundOpacity.BALANCED;
    private BackgroundOpacity detailsBackgroundOpacity = BackgroundOpacity.OFF;
    private boolean panelShadow = true;
    private boolean textShadow = true;

    public static LocatorHudConfig load() {
        if (!Files.isRegularFile(PATH)) {
            LocatorHudConfig defaults = new LocatorHudConfig();
            defaults.save();
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(PATH)) {
            LocatorHudConfig loaded = GSON.fromJson(reader, LocatorHudConfig.class);
            if (loaded == null) {
                loaded = new LocatorHudConfig();
            }
            loaded.validate();
            return loaded;
        } catch (IOException | RuntimeException exception) {
            LocatorHudConfig defaults = new LocatorHudConfig();
            defaults.validate();
            return defaults;
        }
    }

    public void save() {
        validate();
        try {
            Files.createDirectories(PATH.getParent());
            Path temporary = PATH.resolveSibling(PATH.getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(this, writer);
            }
            try {
                Files.move(temporary, PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
            // A read-only config directory must not make the game unusable.
        }
    }

    public void reset() {
        LocatorHudConfig defaults = new LocatorHudConfig();
        defaults.validate();
        this.enabled = defaults.enabled;
        this.mainPanelEnabled = defaults.mainPanelEnabled;
        this.detailsPanelEnabled = defaults.detailsPanelEnabled;
        this.corner = defaults.corner;
        this.coordinateDisplay = defaults.coordinateDisplay;
        this.precision = defaults.precision;
        this.worldNameEnabled = defaults.worldNameEnabled;
        this.worldNameDisplay = defaults.worldNameDisplay;
        this.viewAnglesEnabled = defaults.viewAnglesEnabled;
        this.viewAnglePrecision = defaults.viewAnglePrecision;
        this.biomeEnabled = defaults.biomeEnabled;
        this.targetBlockEnabled = defaults.targetBlockEnabled;
        this.targetFluidEnabled = defaults.targetFluidEnabled;
        this.targetEntityEnabled = defaults.targetEntityEnabled;
        this.autoHideEmptyTargetValues = defaults.autoHideEmptyTargetValues;
        this.hudScale = defaults.hudScale;
        this.detailsCorner = defaults.detailsCorner;
        this.detailsHudScale = defaults.detailsHudScale;
        this.palette = defaults.palette;
        this.backgroundOpacity = defaults.backgroundOpacity;
        this.detailsBackgroundOpacity = defaults.detailsBackgroundOpacity;
        this.panelShadow = defaults.panelShadow;
        this.textShadow = defaults.textShadow;
        save();
    }

    public boolean enabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public void toggleEnabled() {
        setEnabled(!this.enabled);
    }

    public boolean mainPanelEnabled() {
        return this.mainPanelEnabled;
    }

    public void setMainPanelEnabled(boolean mainPanelEnabled) {
        this.mainPanelEnabled = mainPanelEnabled;
        save();
    }

    public boolean detailsPanelEnabled() {
        return this.detailsPanelEnabled;
    }

    public void setDetailsPanelEnabled(boolean detailsPanelEnabled) {
        this.detailsPanelEnabled = detailsPanelEnabled;
        save();
    }

    public HudCorner corner() {
        return this.corner;
    }

    public void setCorner(HudCorner corner) {
        this.corner = corner;
        save();
    }

    public CoordinatePrecision precision() {
        return this.precision;
    }

    public CoordinateDisplayMode coordinateDisplay() {
        return this.coordinateDisplay;
    }

    public void setCoordinateDisplay(CoordinateDisplayMode coordinateDisplay) {
        this.coordinateDisplay = coordinateDisplay;
        save();
    }

    public void setPrecision(CoordinatePrecision precision) {
        this.precision = precision;
        save();
    }

    public boolean worldNameEnabled() {
        return worldNameDisplay().showsWorld();
    }

    public void setWorldNameEnabled(boolean worldNameEnabled) {
        setWorldNameDisplay(WorldNameDisplay.fromLegacy(worldNameEnabled));
    }

    public WorldNameDisplay worldNameDisplay() {
        return this.worldNameDisplay != null
            ? this.worldNameDisplay
            : WorldNameDisplay.fromLegacy(this.worldNameEnabled);
    }

    public void setWorldNameDisplay(WorldNameDisplay worldNameDisplay) {
        this.worldNameDisplay = worldNameDisplay != null ? worldNameDisplay : WorldNameDisplay.BEHIND;
        this.worldNameEnabled = this.worldNameDisplay.showsWorld();
        save();
    }

    public boolean viewAnglesEnabled() {
        return this.viewAnglesEnabled;
    }

    public void setViewAnglesEnabled(boolean viewAnglesEnabled) {
        this.viewAnglesEnabled = viewAnglesEnabled;
        save();
    }

    public ViewAnglePrecision viewAnglePrecision() {
        return this.viewAnglePrecision;
    }

    public void setViewAnglePrecision(ViewAnglePrecision viewAnglePrecision) {
        this.viewAnglePrecision = viewAnglePrecision;
        save();
    }

    public boolean biomeEnabled() {
        return this.biomeEnabled;
    }

    public void setBiomeEnabled(boolean biomeEnabled) {
        this.biomeEnabled = biomeEnabled;
        save();
    }

    public boolean targetBlockEnabled() {
        return this.targetBlockEnabled;
    }

    public void setTargetBlockEnabled(boolean targetBlockEnabled) {
        this.targetBlockEnabled = targetBlockEnabled;
        save();
    }

    public boolean targetFluidEnabled() {
        return this.targetFluidEnabled;
    }

    public void setTargetFluidEnabled(boolean targetFluidEnabled) {
        this.targetFluidEnabled = targetFluidEnabled;
        save();
    }

    public boolean targetEntityEnabled() {
        return this.targetEntityEnabled;
    }

    public void setTargetEntityEnabled(boolean targetEntityEnabled) {
        this.targetEntityEnabled = targetEntityEnabled;
        save();
    }

    public boolean autoHideEmptyTargetValues() {
        return this.autoHideEmptyTargetValues;
    }

    public void setAutoHideEmptyTargetValues(boolean autoHideEmptyTargetValues) {
        this.autoHideEmptyTargetValues = autoHideEmptyTargetValues;
        save();
    }

    public HudScale hudScale() {
        return this.hudScale;
    }

    public void setHudScale(HudScale hudScale) {
        this.hudScale = hudScale;
        save();
    }

    public HudCorner detailsCorner() {
        return this.detailsCorner;
    }

    public void setDetailsCorner(HudCorner detailsCorner) {
        this.detailsCorner = detailsCorner;
        save();
    }

    public HudScale detailsHudScale() {
        return this.detailsHudScale;
    }

    public void setDetailsHudScale(HudScale detailsHudScale) {
        this.detailsHudScale = detailsHudScale;
        save();
    }

    public ColorPalette palette() {
        return this.palette;
    }

    public void setPalette(ColorPalette palette) {
        this.palette = palette;
        save();
    }

    public BackgroundOpacity backgroundOpacity() {
        return this.backgroundOpacity;
    }

    public void setBackgroundOpacity(BackgroundOpacity backgroundOpacity) {
        this.backgroundOpacity = backgroundOpacity;
        save();
    }

    public BackgroundOpacity detailsBackgroundOpacity() {
        return this.detailsBackgroundOpacity;
    }

    public void setDetailsBackgroundOpacity(BackgroundOpacity detailsBackgroundOpacity) {
        this.detailsBackgroundOpacity = detailsBackgroundOpacity;
        save();
    }

    public boolean panelShadow() {
        return this.panelShadow;
    }

    public void setPanelShadow(boolean panelShadow) {
        this.panelShadow = panelShadow;
        save();
    }

    public boolean textShadow() {
        return this.textShadow;
    }

    public void setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
        save();
    }

    private void validate() {
        if (this.corner == null) {
            this.corner = HudCorner.TOP_LEFT;
        }
        if (this.precision == CoordinatePrecision.BLOCK) {
            this.coordinateDisplay = CoordinateDisplayMode.BLOCK_ONLY;
            this.precision = CoordinatePrecision.ONE_DECIMAL;
        } else if (this.precision == null) {
            this.precision = CoordinatePrecision.ONE_DECIMAL;
        }
        if (this.coordinateDisplay == null) {
            this.coordinateDisplay = CoordinateDisplayMode.DECIMAL_ONLY;
        }
        if (this.worldNameDisplay == null) {
            this.worldNameDisplay = WorldNameDisplay.fromLegacy(this.worldNameEnabled);
        }
        this.worldNameEnabled = this.worldNameDisplay.showsWorld();
        if (this.viewAnglePrecision == null) {
            this.viewAnglePrecision = ViewAnglePrecision.WHOLE;
        }
        if (this.hudScale == null) {
            this.hudScale = HudScale.NORMAL;
        }
        if (this.detailsCorner == null) {
            this.detailsCorner = HudCorner.TOP_RIGHT;
        }
        if (this.detailsHudScale == null) {
            this.detailsHudScale = HudScale.COMPACT;
        }
        if (this.palette == null) {
            this.palette = ColorPalette.OCEAN;
        }
        if (this.backgroundOpacity == null) {
            this.backgroundOpacity = BackgroundOpacity.BALANCED;
        }
        if (this.detailsBackgroundOpacity == null) {
            this.detailsBackgroundOpacity = BackgroundOpacity.OFF;
        }
    }
}
