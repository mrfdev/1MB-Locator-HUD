package dev.mrfdev.locatorhud.config;

import dev.mrfdev.locatorhud.CoordinateCopyFormat;
import dev.mrfdev.locatorhud.CoordinateDisplayMode;
import dev.mrfdev.locatorhud.CoordinatePrecision;
import dev.mrfdev.locatorhud.HudScale;
import dev.mrfdev.locatorhud.PanelGeometry.Offset;
import dev.mrfdev.locatorhud.PanelPlacementPolicy;
import dev.mrfdev.locatorhud.PanelWidth;
import dev.mrfdev.locatorhud.PanelWidthLimits;
import dev.mrfdev.locatorhud.TargetNameMode;
import dev.mrfdev.locatorhud.ViewAnglePrecision;
import dev.mrfdev.locatorhud.ViewDirectionDisplay;
import dev.mrfdev.locatorhud.WorldNameDisplay;
import java.util.Objects;

public final class LocatorHudSettings {
    private boolean enabled = true;
    private boolean accessibilitySettingsEnabled = false;
    private boolean mainPanelEnabled = true;
    private boolean detailsPanelEnabled = true;
    private HudCorner corner = HudCorner.TOP_LEFT;
    private int mainPanelOffsetX;
    private int mainPanelOffsetY;
    private CoordinateDisplayMode coordinateDisplay = CoordinateDisplayMode.DECIMAL_ONLY;
    private CoordinatePrecision precision = CoordinatePrecision.ONE_DECIMAL;
    private boolean coordinateLensEnabled = false;
    private CoordinateCopyFormat coordinateCopyFormat = CoordinateCopyFormat.PLAIN;
    // Retained as a serialized mirror so older versions can still read this configuration.
    private boolean worldNameEnabled = true;
    private WorldNameDisplay worldNameDisplay;
    // Retained as a serialized mirror so older versions can still read this configuration.
    private boolean viewDirectionEnabled = true;
    private ViewDirectionDisplay viewDirectionDisplay;
    private boolean viewAnglesEnabled = false;
    private ViewAnglePrecision viewAnglePrecision = ViewAnglePrecision.WHOLE;
    private boolean biomeEnabled = false;
    private boolean biomeTransitionEnabled = false;
    private boolean movementSpeedEnabled = false;
    private boolean targetBlockEnabled = false;
    private boolean targetFluidEnabled = false;
    private boolean targetEntityEnabled = false;
    private TargetNameMode targetNameMode = TargetNameMode.API_ACCURATE;
    private boolean autoHideEmptyTargetValues = false;
    private boolean targetLingerEnabled = false;
    private HudScale hudScale = HudScale.NORMAL;
    private PanelWidth mainPanelMinimumWidth = PanelWidth.AUTO;
    private PanelWidth mainPanelMaximumWidth = PanelWidth.AUTO;
    private HudCorner detailsCorner = HudCorner.TOP_RIGHT;
    private int detailsPanelOffsetX;
    private int detailsPanelOffsetY;
    private HudScale detailsHudScale = HudScale.COMPACT;
    private PanelWidth detailsPanelMinimumWidth = PanelWidth.AUTO;
    private PanelWidth detailsPanelMaximumWidth = PanelWidth.AUTO;
    private ColorPalette palette = ColorPalette.OCEAN;
    private boolean biomeThemeOverrideEnabled = false;
    private BackgroundOpacity backgroundOpacity = BackgroundOpacity.BALANCED;
    private BackgroundOpacity detailsBackgroundOpacity = BackgroundOpacity.OFF;
    private boolean panelShadow = true;
    private boolean textShadow = true;

    public LocatorHudSettings() {
    }

    public static LocatorHudSettings defaults() {
        LocatorHudSettings settings = new LocatorHudSettings();
        settings.validate();
        return settings;
    }

    public LocatorHudSettings copy() {
        LocatorHudSettings copy = new LocatorHudSettings();
        copy.replaceWith(this);
        return copy;
    }

    public boolean enabled() {
        return this.enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean accessibilitySettingsEnabled() {
        return this.accessibilitySettingsEnabled;
    }

    void setAccessibilitySettingsEnabled(boolean accessibilitySettingsEnabled) {
        this.accessibilitySettingsEnabled = accessibilitySettingsEnabled;
        normalizeAccessibilityScales();
    }

    public boolean mainPanelEnabled() {
        return this.mainPanelEnabled;
    }

    void setMainPanelEnabled(boolean mainPanelEnabled) {
        this.mainPanelEnabled = mainPanelEnabled;
    }

    public boolean detailsPanelEnabled() {
        return this.detailsPanelEnabled;
    }

    void setDetailsPanelEnabled(boolean detailsPanelEnabled) {
        this.detailsPanelEnabled = detailsPanelEnabled;
    }

    public HudCorner corner() {
        return this.corner;
    }

    void setCorner(HudCorner corner) {
        this.corner = corner;
    }

    public Offset mainPanelOffset() {
        return new Offset(this.mainPanelOffsetX, this.mainPanelOffsetY);
    }

    void setMainPanelPlacement(HudCorner corner, Offset offset) {
        this.corner = Objects.requireNonNull(corner, "corner");
        Offset clamped = PanelPlacementPolicy.clampOffset(offset);
        this.mainPanelOffsetX = clamped.x();
        this.mainPanelOffsetY = clamped.y();
    }

    public CoordinateDisplayMode coordinateDisplay() {
        return this.coordinateDisplay;
    }

    void setCoordinateDisplay(CoordinateDisplayMode coordinateDisplay) {
        this.coordinateDisplay = coordinateDisplay;
    }

    public CoordinatePrecision precision() {
        return this.precision;
    }

    void setPrecision(CoordinatePrecision precision) {
        this.precision = precision;
    }

    public boolean coordinateLensEnabled() {
        return this.coordinateLensEnabled;
    }

    void setCoordinateLensEnabled(boolean coordinateLensEnabled) {
        this.coordinateLensEnabled = coordinateLensEnabled;
    }

    public CoordinateCopyFormat coordinateCopyFormat() {
        return this.coordinateCopyFormat;
    }

    void setCoordinateCopyFormat(CoordinateCopyFormat coordinateCopyFormat) {
        this.coordinateCopyFormat = coordinateCopyFormat;
    }

    public boolean worldNameEnabled() {
        return worldNameDisplay().showsWorld();
    }

    void setWorldNameEnabled(boolean worldNameEnabled) {
        setWorldNameDisplay(WorldNameDisplay.fromLegacy(worldNameEnabled));
    }

    public WorldNameDisplay worldNameDisplay() {
        return this.worldNameDisplay != null
            ? this.worldNameDisplay
            : WorldNameDisplay.fromLegacy(this.worldNameEnabled);
    }

    void setWorldNameDisplay(WorldNameDisplay worldNameDisplay) {
        this.worldNameDisplay = worldNameDisplay != null ? worldNameDisplay : WorldNameDisplay.BEHIND;
        this.worldNameEnabled = this.worldNameDisplay.showsWorld();
    }

    public boolean viewDirectionEnabled() {
        return viewDirectionDisplay().showsDirection();
    }

    void setViewDirectionEnabled(boolean viewDirectionEnabled) {
        setViewDirectionDisplay(ViewDirectionDisplay.fromLegacy(viewDirectionEnabled));
    }

    public ViewDirectionDisplay viewDirectionDisplay() {
        return this.viewDirectionDisplay != null
            ? this.viewDirectionDisplay
            : ViewDirectionDisplay.fromLegacy(this.viewDirectionEnabled);
    }

    void setViewDirectionDisplay(ViewDirectionDisplay viewDirectionDisplay) {
        this.viewDirectionDisplay = viewDirectionDisplay != null
            ? viewDirectionDisplay
            : ViewDirectionDisplay.ON;
        this.viewDirectionEnabled = this.viewDirectionDisplay.showsDirection();
    }

    public boolean viewAnglesEnabled() {
        return this.viewAnglesEnabled;
    }

    void setViewAnglesEnabled(boolean viewAnglesEnabled) {
        this.viewAnglesEnabled = viewAnglesEnabled;
    }

    public ViewAnglePrecision viewAnglePrecision() {
        return this.viewAnglePrecision;
    }

    void setViewAnglePrecision(ViewAnglePrecision viewAnglePrecision) {
        this.viewAnglePrecision = viewAnglePrecision;
    }

    public boolean biomeEnabled() {
        return this.biomeEnabled;
    }

    void setBiomeEnabled(boolean biomeEnabled) {
        this.biomeEnabled = biomeEnabled;
    }

    public boolean biomeTransitionEnabled() {
        return this.biomeTransitionEnabled;
    }

    void setBiomeTransitionEnabled(boolean biomeTransitionEnabled) {
        this.biomeTransitionEnabled = biomeTransitionEnabled;
    }

    public boolean movementSpeedEnabled() {
        return this.movementSpeedEnabled;
    }

    void setMovementSpeedEnabled(boolean movementSpeedEnabled) {
        this.movementSpeedEnabled = movementSpeedEnabled;
    }

    public boolean targetBlockEnabled() {
        return this.targetBlockEnabled;
    }

    void setTargetBlockEnabled(boolean targetBlockEnabled) {
        this.targetBlockEnabled = targetBlockEnabled;
    }

    public boolean targetFluidEnabled() {
        return this.targetFluidEnabled;
    }

    void setTargetFluidEnabled(boolean targetFluidEnabled) {
        this.targetFluidEnabled = targetFluidEnabled;
    }

    public boolean targetEntityEnabled() {
        return this.targetEntityEnabled;
    }

    void setTargetEntityEnabled(boolean targetEntityEnabled) {
        this.targetEntityEnabled = targetEntityEnabled;
    }

    public TargetNameMode targetNameMode() {
        return this.targetNameMode;
    }

    void setTargetNameMode(TargetNameMode targetNameMode) {
        this.targetNameMode = targetNameMode;
    }

    public boolean autoHideEmptyTargetValues() {
        return this.autoHideEmptyTargetValues;
    }

    void setAutoHideEmptyTargetValues(boolean autoHideEmptyTargetValues) {
        this.autoHideEmptyTargetValues = autoHideEmptyTargetValues;
    }

    public boolean targetLingerEnabled() {
        return this.targetLingerEnabled;
    }

    void setTargetLingerEnabled(boolean targetLingerEnabled) {
        this.targetLingerEnabled = targetLingerEnabled;
    }

    public HudScale hudScale() {
        return this.hudScale;
    }

    void setHudScale(HudScale hudScale) {
        this.hudScale = hudScale;
    }

    public PanelWidthLimits mainPanelWidthLimits() {
        return new PanelWidthLimits(this.mainPanelMinimumWidth, this.mainPanelMaximumWidth);
    }

    public PanelWidth mainPanelMinimumWidth() {
        return this.mainPanelMinimumWidth;
    }

    void setMainPanelMinimumWidth(PanelWidth minimumWidth) {
        setMainPanelWidthLimits(mainPanelWidthLimits().withMinimum(minimumWidth));
    }

    public PanelWidth mainPanelMaximumWidth() {
        return this.mainPanelMaximumWidth;
    }

    void setMainPanelMaximumWidth(PanelWidth maximumWidth) {
        setMainPanelWidthLimits(mainPanelWidthLimits().withMaximum(maximumWidth));
    }

    private void setMainPanelWidthLimits(PanelWidthLimits limits) {
        this.mainPanelMinimumWidth = limits.minimum();
        this.mainPanelMaximumWidth = limits.maximum();
    }

    public HudCorner detailsCorner() {
        return this.detailsCorner;
    }

    void setDetailsCorner(HudCorner detailsCorner) {
        this.detailsCorner = detailsCorner;
    }

    public Offset detailsPanelOffset() {
        return new Offset(this.detailsPanelOffsetX, this.detailsPanelOffsetY);
    }

    void setDetailsPanelPlacement(HudCorner corner, Offset offset) {
        this.detailsCorner = Objects.requireNonNull(corner, "corner");
        Offset clamped = PanelPlacementPolicy.clampOffset(offset);
        this.detailsPanelOffsetX = clamped.x();
        this.detailsPanelOffsetY = clamped.y();
    }

    public HudScale detailsHudScale() {
        return this.detailsHudScale;
    }

    void setDetailsHudScale(HudScale detailsHudScale) {
        this.detailsHudScale = detailsHudScale;
    }

    public PanelWidthLimits detailsPanelWidthLimits() {
        return new PanelWidthLimits(this.detailsPanelMinimumWidth, this.detailsPanelMaximumWidth);
    }

    public PanelWidth detailsPanelMinimumWidth() {
        return this.detailsPanelMinimumWidth;
    }

    void setDetailsPanelMinimumWidth(PanelWidth minimumWidth) {
        setDetailsPanelWidthLimits(detailsPanelWidthLimits().withMinimum(minimumWidth));
    }

    public PanelWidth detailsPanelMaximumWidth() {
        return this.detailsPanelMaximumWidth;
    }

    void setDetailsPanelMaximumWidth(PanelWidth maximumWidth) {
        setDetailsPanelWidthLimits(detailsPanelWidthLimits().withMaximum(maximumWidth));
    }

    private void setDetailsPanelWidthLimits(PanelWidthLimits limits) {
        this.detailsPanelMinimumWidth = limits.minimum();
        this.detailsPanelMaximumWidth = limits.maximum();
    }

    public ColorPalette palette() {
        return this.palette;
    }

    void setPalette(ColorPalette palette) {
        this.palette = palette;
    }

    public boolean biomeThemeOverrideEnabled() {
        return this.biomeThemeOverrideEnabled;
    }

    void setBiomeThemeOverrideEnabled(boolean biomeThemeOverrideEnabled) {
        this.biomeThemeOverrideEnabled = biomeThemeOverrideEnabled;
    }

    public BackgroundOpacity backgroundOpacity() {
        return this.backgroundOpacity;
    }

    void setBackgroundOpacity(BackgroundOpacity backgroundOpacity) {
        this.backgroundOpacity = backgroundOpacity;
    }

    public BackgroundOpacity detailsBackgroundOpacity() {
        return this.detailsBackgroundOpacity;
    }

    void setDetailsBackgroundOpacity(BackgroundOpacity detailsBackgroundOpacity) {
        this.detailsBackgroundOpacity = detailsBackgroundOpacity;
    }

    public boolean panelShadow() {
        return this.panelShadow;
    }

    void setPanelShadow(boolean panelShadow) {
        this.panelShadow = panelShadow;
    }

    public boolean textShadow() {
        return this.textShadow;
    }

    void setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
    }

    void replaceWith(LocatorHudSettings source) {
        Objects.requireNonNull(source, "source");
        this.enabled = source.enabled;
        this.accessibilitySettingsEnabled = source.accessibilitySettingsEnabled;
        this.mainPanelEnabled = source.mainPanelEnabled;
        this.detailsPanelEnabled = source.detailsPanelEnabled;
        this.corner = source.corner;
        this.mainPanelOffsetX = source.mainPanelOffsetX;
        this.mainPanelOffsetY = source.mainPanelOffsetY;
        this.coordinateDisplay = source.coordinateDisplay;
        this.precision = source.precision;
        this.coordinateLensEnabled = source.coordinateLensEnabled;
        this.coordinateCopyFormat = source.coordinateCopyFormat;
        this.worldNameEnabled = source.worldNameEnabled;
        this.worldNameDisplay = source.worldNameDisplay;
        this.viewDirectionEnabled = source.viewDirectionEnabled;
        this.viewDirectionDisplay = source.viewDirectionDisplay;
        this.viewAnglesEnabled = source.viewAnglesEnabled;
        this.viewAnglePrecision = source.viewAnglePrecision;
        this.biomeEnabled = source.biomeEnabled;
        this.biomeTransitionEnabled = source.biomeTransitionEnabled;
        this.movementSpeedEnabled = source.movementSpeedEnabled;
        this.targetBlockEnabled = source.targetBlockEnabled;
        this.targetFluidEnabled = source.targetFluidEnabled;
        this.targetEntityEnabled = source.targetEntityEnabled;
        this.targetNameMode = source.targetNameMode;
        this.autoHideEmptyTargetValues = source.autoHideEmptyTargetValues;
        this.targetLingerEnabled = source.targetLingerEnabled;
        this.hudScale = source.hudScale;
        this.mainPanelMinimumWidth = source.mainPanelMinimumWidth;
        this.mainPanelMaximumWidth = source.mainPanelMaximumWidth;
        this.detailsCorner = source.detailsCorner;
        this.detailsPanelOffsetX = source.detailsPanelOffsetX;
        this.detailsPanelOffsetY = source.detailsPanelOffsetY;
        this.detailsHudScale = source.detailsHudScale;
        this.detailsPanelMinimumWidth = source.detailsPanelMinimumWidth;
        this.detailsPanelMaximumWidth = source.detailsPanelMaximumWidth;
        this.palette = source.palette;
        this.biomeThemeOverrideEnabled = source.biomeThemeOverrideEnabled;
        this.backgroundOpacity = source.backgroundOpacity;
        this.detailsBackgroundOpacity = source.detailsBackgroundOpacity;
        this.panelShadow = source.panelShadow;
        this.textShadow = source.textShadow;
        validate();
    }

    void validate() {
        if (this.corner == null) {
            this.corner = HudCorner.TOP_LEFT;
        }
        Offset mainOffset = PanelPlacementPolicy.clampOffset(mainPanelOffset());
        this.mainPanelOffsetX = mainOffset.x();
        this.mainPanelOffsetY = mainOffset.y();
        if (this.precision == CoordinatePrecision.BLOCK) {
            this.coordinateDisplay = CoordinateDisplayMode.BLOCK_ONLY;
            this.precision = CoordinatePrecision.ONE_DECIMAL;
        } else if (this.precision == null) {
            this.precision = CoordinatePrecision.ONE_DECIMAL;
        }
        if (this.coordinateDisplay == null) {
            this.coordinateDisplay = CoordinateDisplayMode.DECIMAL_ONLY;
        }
        if (this.coordinateCopyFormat == null) {
            this.coordinateCopyFormat = CoordinateCopyFormat.PLAIN;
        }
        if (this.worldNameDisplay == null) {
            this.worldNameDisplay = WorldNameDisplay.fromLegacy(this.worldNameEnabled);
        }
        this.worldNameEnabled = this.worldNameDisplay.showsWorld();
        if (this.viewDirectionDisplay == null) {
            this.viewDirectionDisplay = ViewDirectionDisplay.fromLegacy(this.viewDirectionEnabled);
        }
        this.viewDirectionEnabled = this.viewDirectionDisplay.showsDirection();
        if (this.viewAnglePrecision == null) {
            this.viewAnglePrecision = ViewAnglePrecision.WHOLE;
        }
        if (this.hudScale == null) {
            this.hudScale = HudScale.NORMAL;
        }
        setMainPanelWidthLimits(PanelWidthLimits.normalized(
            this.mainPanelMinimumWidth,
            this.mainPanelMaximumWidth
        ));
        if (this.detailsCorner == null) {
            this.detailsCorner = HudCorner.TOP_RIGHT;
        }
        Offset detailsOffset = PanelPlacementPolicy.clampOffset(detailsPanelOffset());
        this.detailsPanelOffsetX = detailsOffset.x();
        this.detailsPanelOffsetY = detailsOffset.y();
        if (this.detailsHudScale == null) {
            this.detailsHudScale = HudScale.COMPACT;
        }
        normalizeAccessibilityScales();
        setDetailsPanelWidthLimits(PanelWidthLimits.normalized(
            this.detailsPanelMinimumWidth,
            this.detailsPanelMaximumWidth
        ));
        if (this.targetNameMode == null) {
            this.targetNameMode = TargetNameMode.API_ACCURATE;
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

    private void normalizeAccessibilityScales() {
        if (!this.accessibilitySettingsEnabled) {
            if (this.hudScale != null) {
                this.hudScale = this.hudScale.standardFallback();
            }
            if (this.detailsHudScale != null) {
                this.detailsHudScale = this.detailsHudScale.standardFallback();
            }
        }
    }

}
