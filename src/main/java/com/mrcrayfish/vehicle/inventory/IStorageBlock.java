/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.Container
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IStorageBlock
extends Container,
MenuProvider {
    public NonNullList<ItemStack> getInventory();

    default public int getContainerSize() {
        return this.getInventory().size();
    }

    default public boolean isEmpty() {
        for (ItemStack itemstack : this.getInventory()) {
            if (itemstack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    default public ItemStack getItem(int index) {
        return index >= 0 && index < this.getInventory().size() ? (ItemStack)this.getInventory().get(index) : ItemStack.EMPTY;
    }

    default public ItemStack removeItem(int index, int count) {
        ItemStack stack = ContainerHelper.removeItem(this.getInventory(), (int)index, (int)count);
        if (!stack.isEmpty()) {
            this.setChanged();
        }
        return stack;
    }

    default public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = (ItemStack)this.getInventory().get(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.getInventory().set(index, ItemStack.EMPTY);
        return stack;
    }

    default public void setItem(int index, ItemStack stack) {
        this.getInventory().set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    default public boolean stillValid(Player player) {
        return false;
    }

    default public void clearContent() {
        this.getInventory().clear();
    }
}

