package com.mrcrayfish.vehicle.datagen;

import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRecipeBuilder
{
    private final RecipeSerializer<?> serializer;
    private final Ingredient ingredient;
    private final FluidEntry entry;

    public FluidExtractorRecipeBuilder(RecipeSerializer<?> serializer, Ingredient ingredient, FluidEntry entry)
    {
        this.serializer = serializer;
        this.ingredient = ingredient;
        this.entry = entry;
    }

    public static FluidExtractorRecipeBuilder extracting(Ingredient ingredient, FluidEntry entry)
    {
        return new FluidExtractorRecipeBuilder(ModRecipeSerializers.FLUID_EXTRACTOR.get(), ingredient, entry);
    }

    public void save(Consumer<FinishedRecipe> consumer, String name)
    {
        this.save(consumer, new ResourceLocation(name));
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id)
    {
        consumer.accept(new Result(id, this.serializer, this.ingredient, this.entry));
    }

    public static class Result implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final RecipeSerializer<?> serializer;
        private final Ingredient ingredient;
        private final FluidEntry entry;

        private Result(ResourceLocation id, RecipeSerializer<?> serializer, Ingredient ingredient, FluidEntry entry)
        {
            this.id = id;
            this.serializer = serializer;
            this.ingredient = ingredient;
            this.entry = entry;
        }

        @Override
        public void serializeRecipeData(JsonObject object)
        {
            object.add("ingredient", this.ingredient.toJson());
            object.add("result", this.entry.toJson());
        }

        @Override
        public ResourceLocation getId()
        {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType()
        {
            return this.serializer;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement()
        {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId()
        {
            return null;
        }
    }
}
