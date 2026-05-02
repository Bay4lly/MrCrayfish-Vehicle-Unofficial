package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Author: MrCrayfish
 */
public class InventoryUtil
{
    private static final Random RANDOM = new Random();

    public static void writeInventoryToNBT(CompoundTag compound, String tagName, Container inventory)
    {
        ListTag tagList = new ListTag();
        for(int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if(!stack.isEmpty())
            {
                CompoundTag stackTag = new CompoundTag();
                stackTag.putByte("Slot", (byte) i);
                stack.save(stackTag);
                tagList.add(stackTag);
            }
        }
        compound.put(tagName, tagList);
    }

    public static <T extends Container> T readInventoryToNBT(CompoundTag compound, String tagName, T t)
    {
        if(compound.contains(tagName, Tag.TAG_LIST))
        {
            ListTag tagList = compound.getList(tagName, Tag.TAG_COMPOUND);
            for(int i = 0; i < tagList.size(); i++)
            {
                CompoundTag tagCompound = tagList.getCompound(i);
                byte slot = tagCompound.getByte("Slot");
                if(slot >= 0 && slot < t.getContainerSize())
                {
                    t.setItem(slot, ItemStack.of(tagCompound));
                }
            }
        }
        return t;
    }

    public static void dropInventoryItems(Level worldIn, double x, double y, double z, Container inventory)
    {
        for(int i = 0; i < inventory.getContainerSize(); ++i)
        {
            ItemStack itemstack = inventory.getItem(i);

            if(!itemstack.isEmpty())
            {
                spawnItemStack(worldIn, x, y, z, itemstack);
            }
        }
    }

    public static void spawnItemStack(Level worldIn, double x, double y, double z, ItemStack stack)
    {
        float offsetX = -0.25F + RANDOM.nextFloat() * 0.5F;
        float offsetY = RANDOM.nextFloat() * 0.8F;
        float offsetZ = -0.25F + RANDOM.nextFloat() * 0.5F;

        while(!stack.isEmpty())
        {
            ItemEntity entity = new ItemEntity(worldIn, x + offsetX, y + offsetY, z + offsetZ, stack.split(RANDOM.nextInt(21) + 10));
            entity.setDeltaMovement(RANDOM.nextGaussian() * 0.05D, RANDOM.nextGaussian() * 0.05D + 0.2D, RANDOM.nextGaussian() * 0.05D);
            entity.setDefaultPickUpDelay();
            worldIn.addFreshEntity(entity);
        }
    }

    public static int getItemAmount(Player player, Item item)
    {
        int amount = 0;
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if(!stack.isEmpty() && stack.getItem() == item)
            {
                amount += stack.getCount();
            }
        }
        return amount;
    }

    public static boolean hasItemAndAmount(Player player, Item item, int amount)
    {
        int count = 0;
        for(ItemStack stack : player.getInventory().items)
        {
            if(stack != null && stack.getItem() == item)
            {
                count += stack.getCount();
            }
        }
        return amount <= count;
    }

    public static boolean removeItemWithAmount(Player player, Item item, int amount)
    {
        if(hasItemAndAmount(player, item, amount))
        {
            for(int i = 0; i < player.getInventory().getContainerSize(); i++)
            {
                ItemStack stack = player.getInventory().getItem(i);
                if(!stack.isEmpty() && stack.getItem() == item)
                {
                    if(amount - stack.getCount() < 0)
                    {
                        stack.shrink(amount);
                        return true;
                    }
                    else
                    {
                        amount -= stack.getCount();
                        player.getInventory().items.set(i, ItemStack.EMPTY);
                        if(amount == 0) return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getItemStackAmount(Player player, ItemStack find)
    {
        int count = 0;
        for(ItemStack stack : player.getInventory().items)
        {
            if(!stack.isEmpty() && areItemStacksEqualIgnoreCount(stack, find))
            {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean hasItemStack(Player player, ItemStack find)
    {
        int count = 0;
        for(ItemStack stack : player.getInventory().items)
        {
            if(!stack.isEmpty() && areItemStacksEqualIgnoreCount(stack, find))
            {
                count += stack.getCount();
            }
        }
        return find.getCount() <= count;
    }

    public static boolean hasWorkstationIngredient(Player player, WorkstationIngredient find)
    {
        int count = 0;
        for(ItemStack stack : player.getInventory().items)
        {
            if(!stack.isEmpty() && find.test(stack))
            {
                count += stack.getCount();
            }
        }
        return find.getCount() <= count;
    }

    public static boolean removeItemStack(Player player, ItemStack find)
    {
        int amount = find.getCount();
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if(!stack.isEmpty() && areItemStacksEqualIgnoreCount(stack, find))
            {
                if(amount - stack.getCount() < 0)
                {
                    stack.shrink(amount);
                    return true;
                }
                else
                {
                    amount -= stack.getCount();
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                    if(amount == 0) return true;
                }
            }
        }
        return false;
    }

    public static boolean removeWorkstationIngredient(Player player, WorkstationIngredient find)
    {
        int amount = find.getCount();
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if(!stack.isEmpty() && find.test(stack))
            {
                if(amount - stack.getCount() < 0)
                {
                    stack.shrink(amount);
                    return true;
                }
                else
                {
                    amount -= stack.getCount();
                    player.getInventory().items.set(i, ItemStack.EMPTY);
                    if(amount == 0) return true;
                }
            }
        }
        return false;
    }

    public static boolean areItemStacksEqualIgnoreCount(ItemStack source, ItemStack target)
    {
        if(source.getItem() != target.getItem())
        {
            return false;
        }
        else if(source.getTag() == null && target.getTag() != null)
        {
            return false;
        }
        else
        {
            return (source.getTag() == null || source.getTag().equals(target.getTag())) && source.areCapsCompatible(target);
        }
    }
}
