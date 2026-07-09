package com.mrcrayfish.vehicle.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.material.Fluid;

public record FluidMixerInput(ItemStack ingredient, Fluid fluidOne, Fluid fluidTwo) implements RecipeInput {
    public ItemStack getItem(int slot) {
        if (slot != 0) {
            throw new IllegalArgumentException("No item for index " + slot);
        }
        return this.ingredient;
    }

    public int size() {
        return 1;
    }
}
