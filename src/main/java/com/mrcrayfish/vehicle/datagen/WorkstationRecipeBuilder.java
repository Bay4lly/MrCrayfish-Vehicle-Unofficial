package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipeBuilder
{
    private final ResourceLocation entityId;
    private final List<WorkstationIngredient> ingredients;
    private final List<ICondition> conditions = new ArrayList<>();

    public WorkstationRecipeBuilder(ResourceLocation entityId, List<WorkstationIngredient> ingredients)
    {
        this.entityId = entityId;
        this.ingredients = ingredients;
    }

    public static WorkstationRecipeBuilder crafting(ResourceLocation entityId, List<WorkstationIngredient> ingredients)
    {
        return new WorkstationRecipeBuilder(entityId, ingredients);
    }

    public WorkstationRecipeBuilder addCondition(ICondition condition)
    {
        this.conditions.add(condition);
        return this;
    }

    public void save(RecipeOutput output, String name)
    {
        this.save(output, ResourceLocation.parse(name));
    }

    public void save(RecipeOutput output, ResourceLocation id)
    {
        RecipeOutput target = this.conditions.isEmpty() ? output : output.withConditions(this.conditions.toArray(new ICondition[0]));
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(this.entityId);
        target.accept(id, new WorkstationRecipe(entityType, this.ingredients), null);
    }
}
