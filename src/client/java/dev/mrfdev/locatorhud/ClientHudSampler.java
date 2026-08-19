package dev.mrfdev.locatorhud;

import dev.mrfdev.locatorhud.config.LocatorHudConfig;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ClientHudSampler {
    private static final int TARGET_BLOCK = 1;
    private static final int TARGET_FLUID = 1 << 1;
    private static final int TARGET_ENTITY = 1 << 2;
    private static final double POSITION_DISCONTINUITY_DISTANCE_SQUARED = 16.0D * 16.0D;
    private static final String UNKNOWN_BIOME = "Unknown";
    private static final int UNDERGROUND_DEPTH_BLOCKS = 8;

    private final LocatorHudConfig config;
    private final HudSampleSchedule schedule = new HudSampleSchedule();
    private final MovementSpeedTracker movementSpeedTracker = new MovementSpeedTracker();
    private final TargetValueLinger targetValueLinger = new TargetValueLinger();
    private final BiomeTransitionTracker biomeTransitionTracker = new BiomeTransitionTracker();
    private final BiomeThemeController biomeThemeController = new BiomeThemeController();

    private ClientLevel level;
    private LocalPlayer player;
    private Identifier dimension;
    private OverworldNetherLens.SourceDimension sourceDimension =
        OverworldNetherLens.SourceDimension.UNSUPPORTED;
    private String world = "";
    private String biome = UNKNOWN_BIOME;
    private Optional<BiomeThemeSample> biomeThemeSample = Optional.empty();
    private Optional<BiomeTransitionTracker.Notice> biomeTransition = Optional.empty();
    private double movementSpeed;
    private HudPaletteColors paletteColors;
    private CrosshairTargets targets = CrosshairTargets.empty();
    private boolean hasPreviousPosition;
    private double previousX;
    private double previousY;
    private double previousZ;

    public ClientHudSampler(LocatorHudConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        this.paletteColors = config.palette().colors();
    }

    public void tick(Minecraft client) {
        LocalPlayer currentPlayer = client.player;
        ClientLevel currentLevel = client.level;
        if (currentPlayer == null || currentLevel == null) {
            reset();
            return;
        }

        ensureSession(currentLevel, currentPlayer);
        observePosition(currentPlayer);

        boolean detailsActive = this.config.enabled()
            && this.config.detailsPanelEnabled();
        boolean movementSpeedRequested = detailsActive && this.config.movementSpeedEnabled();
        if (movementSpeedRequested) {
            this.movementSpeed = this.movementSpeedTracker.update(
                currentPlayer.getX(),
                currentPlayer.getZ()
            );
        } else {
            this.movementSpeedTracker.reset();
            this.movementSpeed = 0.0D;
        }

        boolean biomeTransitionRequested = detailsActive
            && this.config.biomeTransitionEnabled();
        boolean biomeThemeRequested = this.config.enabled()
            && this.config.biomeThemeOverrideEnabled();
        boolean biomeRequested = biomeThemeRequested
            || detailsActive && (this.config.biomeEnabled() || biomeTransitionRequested);
        int targetSelection = detailsActive
            && DebugInfoPolicy.allowsTargetDetails(currentPlayer.isReducedDebugInfo())
            ? targetSelection()
            : 0;
        int blockX = currentPlayer.getBlockX();
        int blockY = currentPlayer.getBlockY();
        int blockZ = currentPlayer.getBlockZ();
        HudSampleSchedule.Refresh refresh = this.schedule.advance(
            biomeRequested,
            blockX,
            blockY,
            blockZ,
            targetSelection
        );

        if (refresh.biome()) {
            BiomeSample sample = biomeSample(currentLevel, currentPlayer.blockPosition());
            this.biome = sample.displayName();
            this.biomeThemeSample = sample.themeSample();
        }
        Optional<String> observedBiome = biomeTransitionRequested
            && !UNKNOWN_BIOME.equals(this.biome)
                ? Optional.of(this.biome)
                : Optional.empty();
        this.biomeTransition = this.biomeTransitionTracker.advance(
            biomeTransitionRequested,
            observedBiome
        );
        this.paletteColors = this.biomeThemeController.advance(
            biomeThemeRequested,
            this.config.palette(),
            this.biomeThemeSample
        );

        Optional<CrosshairTargets> targetSample = Optional.empty();
        if (refresh.targets()) {
            targetSample = Optional.of(CrosshairTargetSampler.capture(
                client,
                (targetSelection & TARGET_BLOCK) != 0,
                (targetSelection & TARGET_FLUID) != 0,
                (targetSelection & TARGET_ENTITY) != 0
            ));
        }
        this.targets = this.targetValueLinger.advance(
            detailsActive && this.config.targetLingerEnabled(),
            new TargetValueLinger.Selection(
                (targetSelection & TARGET_BLOCK) != 0,
                (targetSelection & TARGET_FLUID) != 0,
                (targetSelection & TARGET_ENTITY) != 0
            ),
            targetSample
        );
    }

    public HudSnapshot snapshot(Minecraft client) {
        LocalPlayer currentPlayer = client.player;
        ClientLevel currentLevel = client.level;
        if (currentPlayer == null || currentLevel == null) {
            reset();
            return null;
        }

        ensureSession(currentLevel, currentPlayer);
        observePosition(currentPlayer);
        return new HudSnapshot(
            currentPlayer.getX(),
            currentPlayer.getY(),
            currentPlayer.getZ(),
            currentPlayer.isReducedDebugInfo(),
            currentPlayer.getDirection().getName(),
            currentPlayer.getYRot(),
            currentPlayer.getXRot(),
            this.world,
            this.sourceDimension,
            this.biome,
            this.biomeTransition,
            this.movementSpeed,
            this.paletteColors,
            this.targets
        );
    }

    public void reset() {
        this.level = null;
        this.player = null;
        this.dimension = null;
        this.sourceDimension = OverworldNetherLens.SourceDimension.UNSUPPORTED;
        this.world = "";
        resetTransientSamples();
        this.hasPreviousPosition = false;
    }

    private void ensureSession(ClientLevel currentLevel, LocalPlayer currentPlayer) {
        Identifier currentDimension = currentLevel.dimension().identifier();
        if (this.level == currentLevel
            && this.player == currentPlayer
            && currentDimension.equals(this.dimension)) {
            return;
        }

        this.level = currentLevel;
        this.player = currentPlayer;
        this.dimension = currentDimension;
        this.sourceDimension = OverworldNetherLens.classify(
            currentDimension.getNamespace(),
            currentDimension.getPath()
        );
        this.world = WorldNameFormatter.fromIdentifier(
            currentDimension.getNamespace(),
            currentDimension.getPath()
        );
        resetTransientSamples();
        this.hasPreviousPosition = false;
    }

    private void observePosition(LocalPlayer currentPlayer) {
        double x = currentPlayer.getX();
        double y = currentPlayer.getY();
        double z = currentPlayer.getZ();
        if (this.hasPreviousPosition) {
            double deltaX = x - this.previousX;
            double deltaY = y - this.previousY;
            double deltaZ = z - this.previousZ;
            double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distanceSquared > POSITION_DISCONTINUITY_DISTANCE_SQUARED) {
                resetTransientSamples();
            }
        }
        this.previousX = x;
        this.previousY = y;
        this.previousZ = z;
        this.hasPreviousPosition = true;
    }

    private int targetSelection() {
        int selection = 0;
        if (this.config.targetBlockEnabled()) {
            selection |= TARGET_BLOCK;
        }
        if (this.config.targetFluidEnabled()) {
            selection |= TARGET_FLUID;
        }
        if (this.config.targetEntityEnabled()) {
            selection |= TARGET_ENTITY;
        }
        return selection;
    }

    private void resetTransientSamples() {
        this.schedule.reset();
        this.movementSpeedTracker.reset();
        this.targetValueLinger.reset();
        this.biomeTransitionTracker.reset();
        this.biomeThemeController.reset();
        this.biome = UNKNOWN_BIOME;
        this.biomeThemeSample = Optional.empty();
        this.biomeTransition = Optional.empty();
        this.movementSpeed = 0.0D;
        this.paletteColors = this.config.palette().colors();
        this.targets = CrosshairTargets.empty();
    }

    private static BiomeSample biomeSample(ClientLevel level, BlockPos position) {
        Holder<Biome> biomeHolder = level.getBiome(position);
        Optional<Identifier> identifier = biomeHolder.unwrapKey().map(key -> key.identifier());
        String displayName = identifier
            .map(value -> WorldNameFormatter.fromIdentifier(
                value.getNamespace(),
                value.getPath()
            ))
            .orElse(UNKNOWN_BIOME);
        int surfaceY = level.getHeight(
            Heightmap.Types.MOTION_BLOCKING,
            position.getX(),
            position.getZ()
        );
        boolean underground = position.getY() + UNDERGROUND_DEPTH_BLOCKS < surfaceY;
        Optional<BiomeThemeSample> themeSample = identifier.map(value -> new BiomeThemeSample(
            value.toString(),
            biomeHolder.value().getBaseTemperature(),
            underground
        ));
        return new BiomeSample(displayName, themeSample);
    }

    private record BiomeSample(String displayName, Optional<BiomeThemeSample> themeSample) {
        private BiomeSample {
            Objects.requireNonNull(displayName, "displayName");
            Objects.requireNonNull(themeSample, "themeSample");
        }
    }
}
