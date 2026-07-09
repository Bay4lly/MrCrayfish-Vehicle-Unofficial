/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.FuelDrumBlock;
import com.mrcrayfish.vehicle.tileentity.IndustrialFuelDrumTileEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class IndustrialFuelDrumBlock
extends FuelDrumBlock {
    @Override
    public int getCapacity() {
        return (Integer)Config.SERVER.industrialFuelDrumCapacity.get();
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialFuelDrumTileEntity(pos, state);
    }
}

