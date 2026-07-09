/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.common.Tags$Blocks
 */
package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.entity.IWheelType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public class SurfaceHelper {
    public static SurfaceType getSurfaceTypeForMaterial(BlockState state) {
        if (state.is(BlockTags.DIRT) || state.is(Tags.Blocks.GRAVELS) || state.is(BlockTags.SAND) || state.is(BlockTags.WOOL) || state.is(Blocks.SPONGE)) {
            return SurfaceType.DIRT;
        }
        if (state.isSolid() || state.is(BlockTags.STONE_ORE_REPLACEABLES) || state.is(BlockTags.SHULKER_BOXES) || state.is(Tags.Blocks.GLASS_BLOCKS)) {
            return SurfaceType.SOLID;
        }
        if (state.is(BlockTags.SNOW) || state.is(BlockTags.ICE) || state.is(BlockTags.LEAVES)) {
            return SurfaceType.SNOW;
        }
        return SurfaceType.NONE;
    }

    public static float getSurfaceModifier(PoweredVehicleEntity vehicle) {
        VehicleProperties properties = vehicle.getProperties();
        List<Wheel> wheels = properties.getWheels();
        if (!vehicle.hasWheelStack() || wheels.isEmpty()) {
            return 1.0f;
        }
        Optional<IWheelType> optional = vehicle.getWheelType();
        if (!optional.isPresent()) {
            return 1.0f;
        }
        int wheelCount = 0;
        float surfaceModifier = 0.0f;
        for (int i = 0; i < wheels.size(); ++i) {
            double wheelX = vehicle.getWheelPositions()[i * 3];
            double wheelY = vehicle.getWheelPositions()[i * 3 + 1];
            double wheelZ = vehicle.getWheelPositions()[i * 3 + 2];
            int x = Mth.floor((double)(vehicle.getX() + wheelX));
            int y = Mth.floor((double)(vehicle.getY() + wheelY - 0.2));
            int z = Mth.floor((double)(vehicle.getZ() + wheelZ));
            BlockState state = vehicle.level().getBlockState(new BlockPos(x, y, z));
            SurfaceType surfaceType = SurfaceHelper.getSurfaceTypeForMaterial(state);
            if (surfaceType == SurfaceType.NONE) continue;
            IWheelType wheelType = optional.get();
            surfaceModifier += 1.0f - surfaceType.wheelFunction.apply(wheelType).floatValue();
            ++wheelCount;
        }
        return 1.0f - surfaceModifier / Math.max(1.0f, (float)wheelCount);
    }

    public static enum SurfaceType {
        SOLID(IWheelType::getRoadMultiplier),
        DIRT(IWheelType::getDirtMultiplier),
        SNOW(IWheelType::getSnowMultiplier),
        NONE(type -> Float.valueOf(0.0f));

        private Function<IWheelType, Float> wheelFunction;

        private SurfaceType(Function<IWheelType, Float> wheelFunction) {
            this.wheelFunction = wheelFunction;
        }
    }
}

