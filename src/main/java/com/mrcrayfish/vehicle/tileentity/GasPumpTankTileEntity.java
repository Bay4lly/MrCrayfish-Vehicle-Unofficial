/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GasPumpTankTileEntity
extends TileFluidHandlerSynced {
    public GasPumpTankTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.GAS_PUMP_TANK.get(), pos, state, (Integer)Config.SERVER.gasPumpCapacity.get());
    }
}

