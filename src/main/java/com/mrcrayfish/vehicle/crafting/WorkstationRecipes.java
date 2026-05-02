package com.mrcrayfish.vehicle.crafting;

import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipes
{
    @Nullable
    public static WorkstationRecipe getRecipe(EntityType<?> entityType, Level world)
    {
        List<WorkstationRecipe> recipes = world.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == ModRecipeTypes.WORKSTATION.get()).map(recipe -> (WorkstationRecipe) recipe).collect(Collectors.toList());
        return recipes.stream().filter(recipe -> recipe.getVehicle() == entityType).findFirst().orElse(null);
    }
}
