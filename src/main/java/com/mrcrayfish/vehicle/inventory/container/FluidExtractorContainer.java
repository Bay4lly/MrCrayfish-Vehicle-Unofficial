/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
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
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class FluidExtractorContainer
extends AbstractContainerMenu {
    private int extractionProgress;
    private int remainingFuel;
    private int maxFuelProgress;
    private int fluidLevel;
    private FluidExtractorTileEntity fluidExtractor;

    public FluidExtractorContainer(int windowId, Container playerInventory, FluidExtractorTileEntity fluidExtractor) {
        super((MenuType)ModContainers.FLUID_EXTRACTOR.get(), windowId);
        int x;
        this.fluidExtractor = fluidExtractor;
        this.addSlot(new FuelSlot(fluidExtractor, 0, 33, 34));
        this.addSlot(new Slot((Container)fluidExtractor, 1, 64, 33));
        for (x = 0; x < 3; ++x) {
            for (int y = 0; y < 9; ++y) {
                this.addSlot(new Slot(playerInventory, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));
            }
        }
        for (x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
        this.addDataSlots(fluidExtractor.getFluidExtractorData());
    }

    public FluidExtractorTileEntity getFluidExtractor() {
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

