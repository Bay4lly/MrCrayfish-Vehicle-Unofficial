/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import java.util.Random;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InventoryUtil {
    private static final Random RANDOM = new Random();

    public static void writeInventoryToNBT(CompoundTag compound, String tagName, Container inventory, HolderLookup.Provider registries) {
        ListTag tagList = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            CompoundTag stackTag = new CompoundTag();
            stackTag.putByte("Slot", (byte)i);
            stack.save(registries, (Tag)stackTag);
            tagList.add(stackTag);
        }
        compound.put(tagName, (Tag)tagList);
    }

    public static <T extends Container> T readInventoryToNBT(CompoundTag compound, String tagName, T t, HolderLookup.Provider registries) {
        if (compound.contains(tagName, 9)) {
            ListTag tagList = compound.getList(tagName, 10);
            for (int i = 0; i < tagList.size(); ++i) {
                CompoundTag tagCompound = tagList.getCompound(i);
                byte slot = tagCompound.getByte("Slot");
                if (slot < 0 || slot >= t.getContainerSize()) continue;
                t.setItem((int)slot, ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tagCompound));
            }
        }
        return t;
    }

    public static void dropInventoryItems(Level worldIn, double x, double y, double z, Container inventory) {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack = inventory.getItem(i);
            if (itemstack.isEmpty()) continue;
            InventoryUtil.spawnItemStack(worldIn, x, y, z, itemstack);
        }
    }

    public static void spawnItemStack(Level worldIn, double x, double y, double z, ItemStack stack) {
        float offsetX = -0.25f + RANDOM.nextFloat() * 0.5f;
        float offsetY = RANDOM.nextFloat() * 0.8f;
        float offsetZ = -0.25f + RANDOM.nextFloat() * 0.5f;
        while (!stack.isEmpty()) {
            ItemEntity entity = new ItemEntity(worldIn, x + (double)offsetX, y + (double)offsetY, z + (double)offsetZ, stack.split(RANDOM.nextInt(21) + 10));
            entity.setDeltaMovement(RANDOM.nextGaussian() * 0.05, RANDOM.nextGaussian() * 0.05 + 0.2, RANDOM.nextGaussian() * 0.05);
            entity.setDefaultPickUpDelay();
            worldIn.addFreshEntity(entity);
        }
    }

    public static int getItemAmount(Player player, Item item) {
        int amount = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || stack.getItem() != item) continue;
            amount += stack.getCount();
        }
        return amount;
    }

    public static boolean hasItemAndAmount(Player player, Item item, int amount) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack == null || stack.getItem() != item) continue;
            count += stack.getCount();
        }
        return amount <= count;
    }

    public static boolean removeItemWithAmount(Player player, Item item, int amount) {
        if (InventoryUtil.hasItemAndAmount(player, item, amount)) {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.isEmpty() || stack.getItem() != item) continue;
                if (amount - stack.getCount() < 0) {
                    stack.shrink(amount);
                    return true;
                }
                player.getInventory().items.set(i, ItemStack.EMPTY);
                if ((amount -= stack.getCount()) != 0) continue;
                return true;
            }
        }
        return false;
    }

    public static int getItemStackAmount(Player player, ItemStack find) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty() || !InventoryUtil.areItemStacksEqualIgnoreCount(stack, find)) continue;
            count += stack.getCount();
        }
        return count;
    }

    public static boolean hasItemStack(Player player, ItemStack find) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty() || !InventoryUtil.areItemStacksEqualIgnoreCount(stack, find)) continue;
            count += stack.getCount();
        }
        return find.getCount() <= count;
    }

    public static boolean hasWorkstationIngredient(Player player, WorkstationIngredient find) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty() || !find.test(stack)) continue;
            count += stack.getCount();
        }
        return find.getCount() <= count;
    }

    public static boolean removeItemStack(Player player, ItemStack find) {
        int amount = find.getCount();
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !InventoryUtil.areItemStacksEqualIgnoreCount(stack, find)) continue;
            if (amount - stack.getCount() < 0) {
                stack.shrink(amount);
                return true;
            }
            player.getInventory().items.set(i, ItemStack.EMPTY);
            if ((amount -= stack.getCount()) != 0) continue;
            return true;
        }
        return false;
    }

    public static boolean removeWorkstationIngredient(Player player, WorkstationIngredient find) {
        int amount = find.getCount();
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !find.test(stack)) continue;
            if (amount - stack.getCount() < 0) {
                stack.shrink(amount);
                return true;
            }
            player.getInventory().items.set(i, ItemStack.EMPTY);
            if ((amount -= stack.getCount()) != 0) continue;
            return true;
        }
        return false;
    }

    public static boolean areItemStacksEqualIgnoreCount(ItemStack source, ItemStack target) {
        return ItemStack.isSameItemSameComponents((ItemStack)source, (ItemStack)target);
    }
}

