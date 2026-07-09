/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.inventory.container.slot;

import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotStorage
extends Slot {
    private StorageInventory storageInventory;

    public SlotStorage(StorageInventory storageInventory, int index, int xPosition, int yPosition) {
        super((Container)storageInventory, index, xPosition, yPosition);
        this.storageInventory = storageInventory;
    }

    public boolean mayPlace(ItemStack stack) {
        return this.storageInventory.isStorageItem(stack);
    }
}

