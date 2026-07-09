/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 */
package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class FluidMixerRecipeBuilder {
    private final FluidEntry inputOne;
    private final FluidEntry inputTwo;
    private final Ingredient ingredient;
    private final FluidEntry output;

    public FluidMixerRecipeBuilder(FluidEntry inputOne, FluidEntry inputTwo, Ingredient ingredient, FluidEntry output) {
        this.inputOne = inputOne;
        this.inputTwo = inputTwo;
        this.ingredient = ingredient;
        this.output = output;
    }

    public static FluidMixerRecipeBuilder mixing(FluidEntry inputOne, FluidEntry inputTwo, Ingredient ingredient, FluidEntry output) {
        return new FluidMixerRecipeBuilder(inputOne, inputTwo, ingredient, output);
    }

    public void save(RecipeOutput output, String name) {
        this.save(output, ResourceLocation.parse((String)name));
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        recipeOutput.accept(id, (Recipe)new FluidMixerRecipe(this.inputOne, this.inputTwo, this.ingredient.getItems()[0], this.output), null);
    }
}

