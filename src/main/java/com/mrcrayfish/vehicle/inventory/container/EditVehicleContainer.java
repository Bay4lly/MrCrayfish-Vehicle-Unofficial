/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EditVehicleContainer
extends AbstractContainerMenu {
    private final Container vehicleInventory;
    private final PoweredVehicleEntity vehicle;

    public EditVehicleContainer(int windowId, Container vehicleInventory, final PoweredVehicleEntity vehicle, Player player, Inventory playerInventory) {
        super((MenuType)ModContainers.EDIT_VEHICLE.get(), windowId);
        int i;
        this.vehicleInventory = vehicleInventory;
        this.vehicle = vehicle;
        this.vehicleInventory.startOpen(player);
        this.addSlot(new Slot(this.vehicleInventory, 0, 8, 17){

            public boolean mayPlace(ItemStack stack) {
                return vehicle.getProperties().getEngineType() != EngineType.NONE && stack.getItem() instanceof EngineItem && ((EngineItem)stack.getItem()).getEngineType() == vehicle.getProperties().getEngineType();
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.vehicleInventory, 1, 8, 35){

            public boolean mayPlace(ItemStack stack) {
                return vehicle.canChangeWheels() && stack.getItem() instanceof WheelItem;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot((Container)playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot((Container)playerInventory, i, 8 + i * 18, 160));
        }
    }

    public Container getVehicleInventory() {
        return this.vehicleInventory;
    }

    public PoweredVehicleEntity getVehicle() {
        return this.vehicle;
    }

    public boolean stillValid(Player player) {
        return this.vehicleInventory.stillValid(player) && this.vehicle.isAlive() && this.vehicle.distanceTo(player) < 8.0f;
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (index < this.vehicleInventory.getContainerSize() ? !this.moveItemStackTo(slotStack, this.vehicleInventory.getContainerSize(), this.slots.size(), true) : (this.getSlot(0).mayPlace(slotStack) ? !this.moveItemStackTo(slotStack, 0, 1, false) : this.vehicleInventory.getContainerSize() <= 1 || !this.moveItemStackTo(slotStack, 1, this.vehicleInventory.getContainerSize(), false))) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.vehicleInventory.stopOpen(player);
    }
}

