package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Author: MrCrayfish
 */
public class ModRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidExtractorRecipe>> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", () -> RecipeType.simple(ResourceLocation.parse("vehicle:fluid_extractor")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidMixerRecipe>> FLUID_MIXER = REGISTER.register("fluid_mixer", () -> RecipeType.simple(ResourceLocation.parse("vehicle:fluid_mixer")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<WorkstationRecipe>> WORKSTATION = REGISTER.register("workstation", () -> RecipeType.simple(ResourceLocation.parse("vehicle:workstation")));
}
