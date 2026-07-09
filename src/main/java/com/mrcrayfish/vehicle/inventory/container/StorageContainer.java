/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.inventory.container.slot.SlotStorage;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class StorageContainer
extends AbstractContainerMenu {
    private final IStorage storageInventory;
    private final int numRows;

    public StorageContainer(int windowId, Container playerInventory, IStorage storageInventory, Player player) {
        super((MenuType)ModContainers.STORAGE.get(), windowId);
        int j;
        int i;
        this.storageInventory = storageInventory;
        this.numRows = storageInventory.getContainerSize() / 9;
        storageInventory.startOpen(player);
        int yOffset = (this.numRows - 4) * 18;
        for (i = 0; i < this.numRows; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new SlotStorage(storageInventory.getInventory(), j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + yOffset));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 161 + yOffset));
        }
    }

    public boolean stillValid(Player playerIn) {
        Entity entity;
        if (this.storageInventory instanceof Entity && !(entity = (Entity)this.storageInventory).isAlive()) {
            return false;
        }
        return this.storageInventory.stillValid(playerIn);
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.numRows * 9 ? !this.moveItemStackTo(itemstack1, this.numRows * 9, this.slots.size(), true) : !this.moveItemStackTo(itemstack1, 0, this.numRows * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.storageInventory.stopOpen(playerIn);
    }

    public Container getStorageInventory() {
        return this.storageInventory;
    }
}

