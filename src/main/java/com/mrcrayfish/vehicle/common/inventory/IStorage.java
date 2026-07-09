/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Container
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IStorage
extends Container {
    public StorageInventory getInventory();

    default public HolderLookup.Provider getRegistries() {
        if (this instanceof Entity) {
            return ((Entity)this).level().registryAccess();
        }
        return null;
    }

    default public int getContainerSize() {
        return this.getInventory().getContainerSize();
    }

    default public boolean isEmpty() {
        return this.getInventory().isEmpty();
    }

    default public ItemStack getItem(int index) {
        return this.getInventory().getItem(index);
    }

    default public ItemStack removeItem(int index, int count) {
        return this.getInventory().removeItem(index, count);
    }

    default public ItemStack removeItemNoUpdate(int index) {
        return this.getInventory().removeItemNoUpdate(index);
    }

    default public void setItem(int index, ItemStack stack) {
        this.getInventory().setItem(index, stack);
    }

    default public int getMaxStackSize() {
        return this.getInventory().getMaxStackSize();
    }

    default public void setChanged() {
        this.getInventory().setChanged();
    }

    default public boolean stillValid(Player player) {
        return this.getInventory().stillValid(player);
    }

    default public void startOpen(Player player) {
        this.getInventory().startOpen(player);
    }

    default public void stopOpen(Player player) {
        this.getInventory().startOpen(player);
    }

    default public boolean canPlaceItem(int index, ItemStack stack) {
        return this.getInventory().canPlaceItem(index, stack);
    }

    default public void clearContent() {
        this.getInventory().clearContent();
    }

    default public boolean isStorageItem(ItemStack stack) {
        return true;
    }

    public Component getStorageName();

    default public MenuProvider getStorageContainerProvider() {
        return new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> new StorageContainer(windowId, (Container)playerInventory, this, playerEntity), this.getStorageName());
    }
}

