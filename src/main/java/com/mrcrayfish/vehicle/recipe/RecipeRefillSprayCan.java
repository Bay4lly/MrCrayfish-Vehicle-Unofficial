/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CustomRecipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.recipe;

import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeRefillSprayCan
extends CustomRecipe {
    public RecipeRefillSprayCan(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput input, Level worldIn) {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof SprayCanItem)) continue;
            if (((SprayCanItem)stack.getItem()).hasColor(stack)) {
                if (!sprayCan.isEmpty()) {
                    return false;
                }
                sprayCan = stack.copy();
                continue;
            }
            if (!emptySprayCan.isEmpty()) {
                return false;
            }
            emptySprayCan = stack.copy();
        }
        return !sprayCan.isEmpty() && !emptySprayCan.isEmpty();
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack sprayCan = ItemStack.EMPTY;
        ItemStack emptySprayCan = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof SprayCanItem)) continue;
            if (((SprayCanItem)stack.getItem()).hasColor(stack)) {
                if (!sprayCan.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                sprayCan = stack.copy();
                continue;
            }
            if (!emptySprayCan.isEmpty()) {
                return ItemStack.EMPTY;
            }
            emptySprayCan = stack.copy();
        }
        if (!sprayCan.isEmpty() && !emptySprayCan.isEmpty()) {
            ItemStack copy = sprayCan.copy();
            ((SprayCanItem)copy.getItem()).refill(copy);
            return copy;
        }
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer)ModRecipeSerializers.REFILL_SPRAY_CAN.get();
    }
}

