package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class BoostTileEntity extends TileEntitySynced
{
    private float speedMultiplier;

    public BoostTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.BOOST.get(), pos, state);
    }

    public BoostTileEntity(BlockPos pos, BlockState state, float defaultSpeedMultiplier)
    {
        super(ModTileEntities.BOOST.get(), pos, state);
        this.speedMultiplier = defaultSpeedMultiplier;
    }

    public float getSpeedMultiplier()
    {
        return speedMultiplier;
    }

    @Override
    public void loadAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries)
    {
        super.loadAdditional(compound, registries);
        if(compound.contains("SpeedMultiplier", Tag.TAG_FLOAT))
        {
            this.speedMultiplier = compound.getFloat("SpeedMultiplier");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries)
    {
        super.saveAdditional(compound, registries);
        compound.putFloat("SpeedMultiplier", this.speedMultiplier);
    }
}

