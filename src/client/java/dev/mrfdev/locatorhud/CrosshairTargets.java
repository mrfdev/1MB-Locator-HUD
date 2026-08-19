package dev.mrfdev.locatorhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public record CrosshairTargets(String block, String fluid, String entity) {
    private static final String NONE = "—";
    private static final double DEBUG_TARGET_RANGE = 20.0D;

    public static CrosshairTargets capture(
        Minecraft client,
        boolean includeBlock,
        boolean includeFluid,
        boolean includeEntity
    ) {
        Entity camera = client.getCameraEntity();
        ClientLevel level = client.level;
        if (camera == null || level == null) {
            return new CrosshairTargets(NONE, NONE, NONE);
        }

        String block = includeBlock ? blockTarget(camera, level) : NONE;
        String fluid = includeFluid ? fluidTarget(camera, level) : NONE;
        String entity = includeEntity ? entityTarget(client.crosshairPickEntity) : NONE;
        return new CrosshairTargets(block, fluid, entity);
    }

    private static String blockTarget(Entity camera, ClientLevel level) {
        BlockPos position = blockPosition(camera.pick(DEBUG_TARGET_RANGE, 0.0F, false));
        if (position == null) {
            return NONE;
        }
        return TargetNameFormatter.fromRegisteredName(level.getBlockState(position).typeHolder().getRegisteredName());
    }

    private static String fluidTarget(Entity camera, ClientLevel level) {
        BlockPos position = blockPosition(camera.pick(DEBUG_TARGET_RANGE, 0.0F, true));
        if (position == null) {
            return NONE;
        }
        FluidState state = level.getFluidState(position);
        if (state.isEmpty()) {
            return NONE;
        }
        return TargetNameFormatter.fromRegisteredName(state.typeHolder().getRegisteredName());
    }

    private static String entityTarget(Entity entity) {
        if (entity == null) {
            return NONE;
        }
        return TargetNameFormatter.fromRegisteredName(entity.typeHolder().getRegisteredName());
    }

    private static BlockPos blockPosition(HitResult hitResult) {
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        return ((BlockHitResult) hitResult).getBlockPos();
    }
}
