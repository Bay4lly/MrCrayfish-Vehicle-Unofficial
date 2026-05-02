package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class IndustrialFuelDrumTileEntity extends FuelDrumTileEntity
{
    public IndustrialFuelDrumTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.INDUSTRIAL_FUEL_DRUM.get(), pos, state, ModBlocks.INDUSTRIAL_FUEL_DRUM.get().getCapacity());
    }
}
