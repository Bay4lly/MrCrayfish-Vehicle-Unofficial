package com.mrcrayfish.vehicle.common.inventory;

import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public interface IStorage extends Container
{
    StorageInventory getInventory();

    /*@Override
    default int[] getSlotsForFace(Direction side)
    {
        int[] slots = new int[this.getInventory().getSizeInventory()];
        for(int i = 0; i < this.getInventory().getSizeInventory(); i++)
        {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    default boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    default boolean canExtractItem(int index, ItemStack stack, Direction direction)
    {
        return true;
    }
*/
    @Override
    default int getContainerSize()
    {
        return this.getInventory().getContainerSize();
    }

    @Override
    default boolean isEmpty()
    {
        return this.getInventory().isEmpty();
    }

    @Override
    default ItemStack getItem(int index)
    {
        return this.getInventory().getItem(index);
    }

    @Override
    default ItemStack removeItem(int index, int count)
    {
        return this.getInventory().removeItem(index, count);
    }

    @Override
    default ItemStack removeItemNoUpdate(int index)
    {
        return this.getInventory().removeItemNoUpdate(index);
    }

    @Override
    default void setItem(int index, ItemStack stack)
    {
        this.getInventory().setItem(index, stack);
    }

    @Override
    default int getMaxStackSize()
    {
        return this.getInventory().getMaxStackSize();
    }

    @Override
    default void setChanged()
    {
        this.getInventory().setChanged();
    }

    @Override
    default boolean stillValid(Player player)
    {
        return this.getInventory().stillValid(player);
    }

    @Override
    default void startOpen(Player player)
    {
        this.getInventory().startOpen(player);
    }

    @Override
    default void stopOpen(Player player)
    {
        this.getInventory().startOpen(player);
    }

    @Override
    default boolean canPlaceItem(int index, ItemStack stack)
    {
        return getInventory().canPlaceItem(index, stack);
    }

    @Override
    default void clearContent()
    {
        getInventory().clearContent();
    }

    default boolean isStorageItem(ItemStack stack)
    {
        return true;
    }

    Component getStorageName();

    default MenuProvider getStorageContainerProvider()
    {
        return new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> new StorageContainer(windowId, playerInventory, this, playerEntity), this.getStorageName());
    }
}
