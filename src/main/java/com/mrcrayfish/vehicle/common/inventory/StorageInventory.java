package com.mrcrayfish.vehicle.common.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class StorageInventory extends SimpleContainer implements MenuProvider
{
    private IStorage wrapper;

    public StorageInventory(IStorage wrapper, int size)
    {
        super(size);
        this.wrapper = wrapper;
    }

    public boolean isStorageItem(ItemStack stack)
    {
        return this.wrapper.isStorageItem(stack);
    }

    @Override
    public Component getDisplayName()
    {
        return this.wrapper.getStorageName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
    {
        return this.wrapper.getStorageContainerProvider().createMenu(windowId, playerInventory, playerEntity);
    }

    public ListTag createTag()
    {
        ListTag tagList = new ListTag();
        for(int i = 0; i < this.getContainerSize(); i++)
        {
            ItemStack stack = this.getItem(i);
            if(!stack.isEmpty())
            {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putByte("Slot", (byte) i);
                stack.save(slotTag);
                tagList.add(slotTag);
            }
        }
        return tagList;
    }

    @Override
    public void fromTag(ListTag tagList)
    {
        this.clearContent();
        for(int i = 0; i < tagList.size(); i++)
        {
            CompoundTag slotTag = tagList.getCompound(i);
            byte slot = slotTag.getByte("Slot");
            if(slot >= 0 && slot < this.getContainerSize())
            {
                this.setItem(slot, ItemStack.of(slotTag));
            }
        }
    }
}
