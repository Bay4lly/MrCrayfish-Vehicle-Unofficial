package com.mrcrayfish.vehicle.inventory.container;

import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class EditVehicleContainer extends AbstractContainerMenu
{
    private final Container vehicleInventory;
    private final PoweredVehicleEntity vehicle;

    public EditVehicleContainer(int windowId, Container vehicleInventory, PoweredVehicleEntity vehicle, Player player, Inventory playerInventory)
    {
        super(ModContainers.EDIT_VEHICLE.get(), windowId);
        this.vehicleInventory = vehicleInventory;
        this.vehicle = vehicle;

        this.vehicleInventory.startOpen(player);

        this.addSlot(new Slot(EditVehicleContainer.this.vehicleInventory, 0, 8, 17)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return vehicle.getProperties().getEngineType() != EngineType.NONE && stack.getItem() instanceof EngineItem && ((EngineItem) stack.getItem()).getEngineType() == vehicle.getProperties().getEngineType();
            }

            @Override
            public int getMaxStackSize()
            {
                return 1;
            }
        });

        this.addSlot(new Slot(EditVehicleContainer.this.vehicleInventory, 1, 8, 35)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return vehicle.canChangeWheels() && stack.getItem() instanceof WheelItem;
            }

            @Override
            public int getMaxStackSize()
            {
                return 1;
            }
        });

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160));
        }
    }

    public Container getVehicleInventory()
    {
        return vehicleInventory;
    }

    public PoweredVehicleEntity getVehicle()
    {
        return vehicle;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return vehicleInventory.stillValid(player) && vehicle.isAlive() && vehicle.distanceTo(player) < 8.0F;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot != null && slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if(index < vehicleInventory.getContainerSize())
            {
                if(!this.moveItemStackTo(slotStack, vehicleInventory.getContainerSize(), slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(this.getSlot(0).mayPlace(slotStack))
            {
                if(!this.moveItemStackTo(slotStack, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(vehicleInventory.getContainerSize() <= 1 || !this.moveItemStackTo(slotStack, 1, vehicleInventory.getContainerSize(), false))
            {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }

        return stack;
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        vehicleInventory.stopOpen(player);
    }
}
