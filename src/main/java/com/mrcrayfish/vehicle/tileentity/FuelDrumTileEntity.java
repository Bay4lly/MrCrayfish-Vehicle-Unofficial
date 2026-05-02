package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class FuelDrumTileEntity extends TileFluidHandlerSynced
{
    public FuelDrumTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.FUEL_DRUM.get(), pos, state, ModBlocks.FUEL_DRUM.get().getCapacity());
    }

    public FuelDrumTileEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, int capacity)
    {
        super(tileEntityType, pos, state, capacity);
    }

    public boolean hasFluid()
    {
        return !this.tank.getFluid().isEmpty();
    }

    public int getAmount()
    {
        return this.tank.getFluidAmount();
    }

    public int getCapacity()
    {
        return this.tank.getCapacity();
    }
}
