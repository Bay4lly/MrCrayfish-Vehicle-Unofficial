package com.mrcrayfish.vehicle.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipeBuilder
{
    private final RecipeSerializer<?> serializer;
    private final ResourceLocation entityId;
    private final List<WorkstationIngredient> ingredients;
    private final List<ICondition> conditions = new ArrayList<>();

    public WorkstationRecipeBuilder(RecipeSerializer<?> serializer, ResourceLocation entityId, List<WorkstationIngredient> ingredients)
    {
        this.serializer = serializer;
        this.entityId = entityId;
        this.ingredients = ingredients;
    }

    public static WorkstationRecipeBuilder crafting(ResourceLocation entityId, List<WorkstationIngredient> ingredients)
    {
        return new WorkstationRecipeBuilder(ModRecipeSerializers.WORKSTATION.get(), entityId, ingredients);
    }

    public WorkstationRecipeBuilder addCondition(ICondition condition)
    {
        this.conditions.add(condition);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, String name)
    {
        this.save(consumer, new ResourceLocation(name));
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id)
    {
        consumer.accept(new Result(id, this.serializer, this.entityId, this.ingredients, this.conditions));
    }

    public static class Result implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final ResourceLocation entityId;
        private final List<WorkstationIngredient> ingredients;
        private final List<ICondition> conditions;
        private final RecipeSerializer<?> serializer;

        private Result(ResourceLocation id, RecipeSerializer<?> serializer, ResourceLocation entityId, List<WorkstationIngredient> ingredients, List<ICondition> conditions)
        {
            this.id = id;
            this.serializer = serializer;
            this.entityId = entityId;
            this.ingredients = ingredients;
            this.conditions = conditions;
        }

        @Override
        public void serializeRecipeData(JsonObject object)
        {
            object.addProperty("vehicle", this.entityId.toString());

            JsonArray conditions = new JsonArray();
            this.conditions.forEach(condition -> conditions.add(CraftingHelper.serialize(condition)));
            if(conditions.size() > 0)
            {
                object.add("conditions", conditions);
            }

            JsonArray materials = new JsonArray();
            this.ingredients.forEach(ingredient -> materials.add(ingredient.toJson()));
            object.add("materials", materials);
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
