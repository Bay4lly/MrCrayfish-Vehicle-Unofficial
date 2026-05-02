package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
public class GasPumpTankTileEntity extends TileFluidHandlerSynced
{
    public GasPumpTankTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.GAS_PUMP_TANK.get(), pos, state, Config.SERVER.gasPumpCapacity.get());
    }
}