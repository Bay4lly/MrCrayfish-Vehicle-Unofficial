package com.mrcrayfish.vehicle.tileentity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

/**
 * Author: MrCrayfish
 */
public interface IFluidTankWriter
{
    void writeTanks(HolderLookup.Provider registries, CompoundTag compound);

    boolean areTanksEmpty();
}