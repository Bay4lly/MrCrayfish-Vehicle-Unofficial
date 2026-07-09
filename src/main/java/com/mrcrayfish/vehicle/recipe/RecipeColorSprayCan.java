/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CustomRecipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.mrcrayfish.vehicle.recipe;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.item.IDyeable;
import java.util.ArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

public class RecipeColorSprayCan
extends CustomRecipe {
    public RecipeColorSprayCan(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput input, Level worldIn) {
        ItemStack dyeableItem = ItemStack.EMPTY;
        ArrayList dyes = Lists.newArrayList();
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof IDyeable) {
                if (!dyeableItem.isEmpty()) {
                    return false;
                }
                dyeableItem = stack.copy();
                continue;
            }
            if (!stack.is(Tags.Items.DYES)) {
                return false;
            }
            dyes.add(stack);
        }
        return !dyeableItem.isEmpty() && !dyes.isEmpty();
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack dyeableItem = ItemStack.EMPTY;
        ArrayList dyes = Lists.newArrayList();
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof IDyeable) {
                if (!dyeableItem.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                dyeableItem = stack.copy();
                continue;
            }
            if (!(stack.getItem() instanceof DyeItem)) {
                return ItemStack.EMPTY;
            }
            dyes.add((DyeItem)stack.getItem());
        }
        return !dyeableItem.isEmpty() && !dyes.isEmpty() ? IDyeable.dyeStack(dyeableItem, dyes) : ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer)ModRecipeSerializers.COLOR_SPRAY_CAN.get();
    }
}

