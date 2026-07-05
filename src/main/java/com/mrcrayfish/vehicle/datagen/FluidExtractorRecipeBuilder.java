package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRecipeBuilder
{
    private final Ingredient ingredient;
    private final FluidEntry entry;

    public FluidExtractorRecipeBuilder(Ingredient ingredient, FluidEntry entry)
    {
        this.ingredient = ingredient;
        this.entry = entry;
    }

    public static FluidExtractorRecipeBuilder extracting(Ingredient ingredient, FluidEntry entry)
    {
        return new FluidExtractorRecipeBuilder(ingredient, entry);
    }

    public void save(RecipeOutput output, String name)
    {
        this.save(output, ResourceLocation.parse(name));
    }

    public void save(RecipeOutput output, ResourceLocation id)
    {
        output.accept(id, new FluidExtractorRecipe(this.ingredient.getItems()[0], this.entry), null);
    }
}
