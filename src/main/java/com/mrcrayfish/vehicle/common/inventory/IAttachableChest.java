/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.common.inventory.IStorage;
import net.minecraft.world.item.ItemStack;

public interface IAttachableChest
extends IStorage {
    public boolean hasChest();

    public void attachChest(ItemStack var1);

    public void removeChest();
}

