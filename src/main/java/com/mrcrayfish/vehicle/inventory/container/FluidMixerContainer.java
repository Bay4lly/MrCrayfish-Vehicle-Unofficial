/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeType
 */
package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.inventory.container.slot.FuelSlot;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class FluidMixerContainer
extends AbstractContainerMenu {
    private int extractionProgress;
    private int remainingFuel;
    private int maxFuelProgress;
    private int blazeLevel;
    private int enderSapLevel;
    private int fueliumLevel;
    private FluidMixerTileEntity fluidExtractor;

    public FluidMixerContainer(int windowId, Inventory playerInventory, FluidMixerTileEntity fluidExtractor) {
        super((MenuType)ModContainers.FLUID_MIXER.get(), windowId);
        int x;
        this.fluidExtractor = fluidExtractor;
        this.addSlot(new FuelSlot(fluidExtractor, 0, 9, 50));
        this.addSlot(new Slot((Container)fluidExtractor, 1, 103, 41));
        for (x = 0; x < 3; ++x) {
            for (int y = 0; y < 9; ++y) {
                this.addSlot(new Slot((Container)playerInventory, y + x * 9 + 9, 8 + y * 18, 98 + x * 18));
            }
        }
        for (x = 0; x < 9; ++x) {
            this.addSlot(new Slot((Container)playerInventory, x, 8 + x * 18, 156));
        }
        this.addDataSlots(fluidExtractor.getFluidMixerData());
    }

    public FluidMixerTileEntity getFluidExtractor() {
        return this.fluidExtractor;
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
            if (index == 0 || index == 1 ? !this.moveItemStackTo(slotStack, 2, 38, true) : (this.fluidExtractor.canPlaceItem(1, slotStack) ? !this.moveItemStackTo(slotStack, 1, 2, false) : (slotStack.getBurnTime(RecipeType.SMELTING) > 0 ? !this.moveItemStackTo(slotStack, 0, 1, false) : (index < 29 ? !this.moveItemStackTo(slotStack, 29, 38, false) : index < 38 && !this.moveItemStackTo(slotStack, 2, 29, false))))) {
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
}

