/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

public class WorkstationContainer
extends AbstractContainerMenu {
    private WorkstationTileEntity workstationTileEntity;
    private BlockPos pos;

    public WorkstationContainer(int windowId, Container playerInventory, WorkstationTileEntity workstationTileEntity) {
        super((MenuType)ModContainers.WORKSTATION.get(), windowId);
        this.workstationTileEntity = workstationTileEntity;
        this.pos = workstationTileEntity.getBlockPos();
        this.addSlot(new Slot(workstationTileEntity, 0, 173, 30){

            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof DyeItem;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(workstationTileEntity, 1, 193, 30){

            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof EngineItem;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(workstationTileEntity, 2, 213, 30){

            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof WheelItem;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 102 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 160));
        }
    }

    public boolean stillValid(Player playerIn) {
        return true;
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (index < 3 ? !this.moveItemStackTo(slotStack, 3, 36, false) : (slotStack.getItem() instanceof DyeItem ? !this.moveItemStackTo(slotStack, 0, 1, false) : (slotStack.getItem() instanceof EngineItem ? !this.moveItemStackTo(slotStack, 1, 2, false) : (slotStack.getItem() instanceof WheelItem ? !this.moveItemStackTo(slotStack, 2, 3, false) : (index < 31 ? !this.moveItemStackTo(slotStack, 31, 39, false) : index < 39 && !this.moveItemStackTo(slotStack, 3, 31, false)))))) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, slotStack);
        }
        return stack;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public WorkstationTileEntity getTileEntity() {
        return this.workstationTileEntity;
    }
}

