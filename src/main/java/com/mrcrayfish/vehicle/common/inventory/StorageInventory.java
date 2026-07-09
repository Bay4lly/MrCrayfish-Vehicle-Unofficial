/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.common.inventory.IStorage;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class StorageInventory
extends SimpleContainer
implements MenuProvider {
    private IStorage wrapper;

    public StorageInventory(IStorage wrapper, int size) {
        super(size);
        this.wrapper = wrapper;
    }

    public boolean isStorageItem(ItemStack stack) {
        return this.wrapper.isStorageItem(stack);
    }

    public Component getDisplayName() {
        return this.wrapper.getStorageName();
    }

    @Nullable
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return this.wrapper.getStorageContainerProvider().createMenu(windowId, playerInventory, playerEntity);
    }

    public ListTag createTag(HolderLookup.Provider registries) {
        ListTag tagList = new ListTag();
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack stack = this.getItem(i);
            if (stack.isEmpty()) continue;
            CompoundTag slotTag = new CompoundTag();
            slotTag.putByte("Slot", (byte)i);
            stack.save(registries, (Tag)slotTag);
            tagList.add(slotTag);
        }
        return tagList;
    }

    public ListTag createTag() {
        return this.createTag(this.wrapper.getRegistries());
    }

    public void fromTag(ListTag tagList, HolderLookup.Provider registries) {
        this.clearContent();
        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag slotTag = tagList.getCompound(i);
            byte slot = slotTag.getByte("Slot");
            if (slot < 0 || slot >= this.getContainerSize()) continue;
            this.setItem(slot, ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)slotTag));
        }
    }

    public void fromTag(ListTag tagList) {
        this.fromTag(tagList, this.wrapper.getRegistries());
    }
}

