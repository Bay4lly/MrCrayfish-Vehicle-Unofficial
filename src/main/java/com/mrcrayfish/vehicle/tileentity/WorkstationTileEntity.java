package com.mrcrayfish.vehicle.tileentity;

import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.inventory.IStorageBlock;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class WorkstationTileEntity extends TileEntitySynced implements IStorageBlock
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    public WorkstationTileEntity(BlockPos pos, BlockState state)
    {
        super(ModTileEntities.WORKSTATION.get(), pos, state);
    }

    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return this.inventory;
    }

    @Override
    public void loadAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries)
    {
        super.loadAdditional(compound, registries);
        ContainerHelper.loadAllItems(compound, this.inventory, registries);
    }

    @Override
    public void saveAdditional(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries)
    {
        super.saveAdditional(compound, registries);
        ContainerHelper.saveAllItems(compound, this.inventory, registries);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack)
    {
        return index != 0 || (stack.getItem() instanceof DyeItem && this.inventory.get(index).getCount() < 1);
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container.vehicle.workstation");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
    {
        return new WorkstationContainer(windowId, playerInventory, this);
    }
}
