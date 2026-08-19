package dev.mrfdev.locatorhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

final class CrosshairTargetSampler {
    private static final double DEBUG_TARGET_RANGE = 20.0D;

    private CrosshairTargetSampler() {
    }

    static CrosshairTargets capture(
        Minecraft client,
        boolean includeBlock,
        boolean includeFluid,
        boolean includeEntity
    ) {
        Entity camera = client.getCameraEntity();
        ClientLevel level = client.level;
        if (camera == null || level == null) {
            return CrosshairTargets.empty();
        }

        TargetDescriptor block = includeBlock
            ? blockTarget(camera, level)
            : TargetDescriptor.empty();
        TargetDescriptor fluid = includeFluid
            ? fluidTarget(camera, level)
            : TargetDescriptor.empty();
        TargetDescriptor entity = includeEntity
            ? entityTarget(client.crosshairPickEntity)
            : TargetDescriptor.empty();
        return new CrosshairTargets(block, fluid, entity);
    }

    private static TargetDescriptor blockTarget(Entity camera, ClientLevel level) {
        // Minecraft.hitResult uses interaction range and may resolve to an entity. Locator HUD's
        // established debug target independently inspects blocks and fluids up to 20 blocks away.
        BlockPos position = blockPosition(camera.pick(DEBUG_TARGET_RANGE, 0.0F, false));
        if (position == null) {
            return TargetDescriptor.empty();
        }
        BlockState state = level.getBlockState(position);
        Block block = state.getBlock();
        String apiAccurate = TargetNameFormatter.fromRegisteredName(
            state.typeHolder().getRegisteredName()
        );
        return new TargetDescriptor(
            apiAccurate,
            localizedName(block.getName(), block.getDescriptionId(), apiAccurate)
        );
    }

    private static TargetDescriptor fluidTarget(Entity camera, ClientLevel level) {
        BlockPos position = blockPosition(camera.pick(DEBUG_TARGET_RANGE, 0.0F, true));
        if (position == null) {
            return TargetDescriptor.empty();
        }
        FluidState state = level.getFluidState(position);
        if (state.isEmpty()) {
            return TargetDescriptor.empty();
        }
        String apiAccurate = TargetNameFormatter.fromRegisteredName(
            state.typeHolder().getRegisteredName()
        );
        return new TargetDescriptor(apiAccurate, fluidFriendlyName(state, apiAccurate));
    }

    private static TargetDescriptor entityTarget(Entity entity) {
        if (entity == null) {
            return TargetDescriptor.empty();
        }
        EntityType<?> type = entity.getType();
        String apiAccurate = TargetNameFormatter.fromRegisteredName(
            entity.typeHolder().getRegisteredName()
        );
        return new TargetDescriptor(
            apiAccurate,
            localizedName(type.getDescription(), type.getDescriptionId(), apiAccurate)
        );
    }

    private static String localizedName(
        Component component,
        String descriptionId,
        String fallback
    ) {
        String value = component.getString();
        return value.isBlank() || value.equals(descriptionId) ? fallback : value;
    }

    private static String fluidFriendlyName(FluidState state, String fallback) {
        String translationKey;
        if (state.getType().isSame(Fluids.WATER)) {
            translationKey = "block.minecraft.water";
        } else if (state.getType().isSame(Fluids.LAVA)) {
            translationKey = "block.minecraft.lava";
        } else {
            translationKey = state.typeHolder().unwrapKey()
                .map(key -> key.identifier().toLanguageKey("block"))
                .orElse(null);
        }
        return translationKey == null
            ? fallback
            : Component.translatableWithFallback(translationKey, fallback).getString();
    }

    private static BlockPos blockPosition(HitResult hitResult) {
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        return ((BlockHitResult) hitResult).getBlockPos();
    }
}
