/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.block.FuelDrumBlock;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class IndustrialFuelDrumTileEntity
extends FuelDrumTileEntity {
    public IndustrialFuelDrumTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.INDUSTRIAL_FUEL_DRUM.get(), pos, state, ((FuelDrumBlock)((Object)ModBlocks.INDUSTRIAL_FUEL_DRUM.get())).getCapacity());
    }
}

