/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 */
package com.mrcrayfish.vehicle.tileentity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IFluidTankWriter {
    public void writeTanks(HolderLookup.Provider var1, CompoundTag var2);

    public boolean areTanksEmpty();
}

