package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.inventory.container.slot.SlotStorage;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class StorageContainer extends AbstractContainerMenu
{
    private final IStorage storageInventory;
    private final int numRows;

    public StorageContainer(int windowId, Container playerInventory, IStorage storageInventory, Player player)
    {
        super(ModContainers.STORAGE.get(), windowId);
        this.storageInventory = storageInventory;
        this.numRows = storageInventory.getContainerSize() / 9;
        storageInventory.startOpen(player);
        int yOffset = (this.numRows - 4) * 18;

        for(int i = 0; i < this.numRows; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new SlotStorage(storageInventory.getInventory(), j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + yOffset));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 161 + yOffset));
        }
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        if(this.storageInventory instanceof Entity)
        {
            Entity entity = (Entity) this.storageInventory;
            if(!entity.isAlive())
            {
                return false;
            }
        }
        return this.storageInventory.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot != null && slot.hasItem())
        {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if(index < this.numRows * 9)
            {
                if(!this.moveItemStackTo(itemstack1, this.numRows * 9, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(itemstack1, 0, this.numRows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if(itemstack1.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player playerIn)
    {
        super.removed(playerIn);
        this.storageInventory.stopOpen(playerIn);
    }

    public Container getStorageInventory()
    {
        return this.storageInventory;
    }
}
