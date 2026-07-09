/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.crafting;

import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WorkstationRecipes {
    @Nullable
    public static WorkstationRecipe getRecipe(EntityType<?> entityType, Level world) {
        return world.getRecipeManager().getAllRecipesFor(ModRecipeTypes.WORKSTATION.get()).stream()
            .map(net.minecraft.world.item.crafting.RecipeHolder::value)
            .filter(recipe -> recipe.getVehicle() == entityType)
            .findFirst()
            .orElse(null);
    }
}

