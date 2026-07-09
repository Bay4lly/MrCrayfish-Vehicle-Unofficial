/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.tileentity.TileEntitySynced;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BoostTileEntity
extends TileEntitySynced {
    private float speedMultiplier;

    public BoostTileEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)ModTileEntities.BOOST.get(), pos, state);
    }

    public BoostTileEntity(BlockPos pos, BlockState state, float defaultSpeedMultiplier) {
        super((BlockEntityType)ModTileEntities.BOOST.get(), pos, state);
        this.speedMultiplier = defaultSpeedMultiplier;
    }

    public float getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("SpeedMultiplier", 5)) {
            this.speedMultiplier = compound.getFloat("SpeedMultiplier");
        }
    }

    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putFloat("SpeedMultiplier", this.speedMultiplier);
    }
}

