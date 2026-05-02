package com.mrcrayfish.vehicle.common;

import com.mrcrayfish.vehicle.entity.IWheelType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Categories materials into a surface type to determine the
 * Author: MrCrayfish
 */
public class SurfaceHelper
{
    public static SurfaceType getSurfaceTypeForMaterial(BlockState state)
    {
        if (state.is(BlockTags.DIRT) || state.is(Tags.Blocks.GRAVEL) || state.is(BlockTags.SAND) || state.is(BlockTags.WOOL) || state.is(Blocks.SPONGE))
        {
            return SurfaceType.DIRT;
        }
        else if (state.isSolid() || state.is(Tags.Blocks.STONE) || state.is(BlockTags.SHULKER_BOXES) || state.is(Tags.Blocks.GLASS))
        {
            return SurfaceType.SOLID;
        }
        else if (state.is(BlockTags.SNOW) || state.is(BlockTags.ICE) || state.is(BlockTags.LEAVES))
        {
            return SurfaceType.SNOW;
        }

        return SurfaceType.NONE;
    }

    public static float getSurfaceModifier(PoweredVehicleEntity vehicle)
    {
        VehicleProperties properties = vehicle.getProperties();
        List<Wheel> wheels = properties.getWheels();
        if(!vehicle.hasWheelStack() || wheels.isEmpty())
            return 1.0F;

        Optional<IWheelType> optional = vehicle.getWheelType();
        if(!optional.isPresent())
            return 1.0F;

        int wheelCount = 0;
        float surfaceModifier = 0F;
        for(int i = 0; i < wheels.size(); i++)
        {
            double wheelX = vehicle.getWheelPositions()[i * 3];
            double wheelY = vehicle.getWheelPositions()[i * 3 + 1];
            double wheelZ = vehicle.getWheelPositions()[i * 3 + 2];
            int x = Mth.floor(vehicle.getX() + wheelX);
            int y = Mth.floor(vehicle.getY() + wheelY - 0.2D);
            int z = Mth.floor(vehicle.getZ() + wheelZ);
            BlockState state = vehicle.level().getBlockState(new BlockPos(x, y, z));
            SurfaceType surfaceType = getSurfaceTypeForMaterial(state);
            if(surfaceType == SurfaceType.NONE)
                continue;
            IWheelType wheelType = optional.get();
            surfaceModifier += (1.0F - surfaceType.wheelFunction.apply(wheelType));
            wheelCount++;
        }
        return 1.0F - (surfaceModifier / Math.max(1F, wheelCount));
    }

    public enum SurfaceType
    {
        SOLID(IWheelType::getRoadMultiplier),
        DIRT(IWheelType::getDirtMultiplier),
        SNOW(IWheelType::getSnowMultiplier),
        NONE(type -> 0F);

        private Function<IWheelType, Float> wheelFunction;

        SurfaceType(Function<IWheelType, Float> wheelFunction)
        {
            this.wheelFunction = wheelFunction;
        }
    }
}
