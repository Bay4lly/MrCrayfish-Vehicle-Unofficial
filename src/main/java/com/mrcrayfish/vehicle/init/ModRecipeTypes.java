package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Reference.MOD_ID);

    public static final RegistryObject<RecipeType<FluidExtractorRecipe>> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", () -> RecipeType.simple(new ResourceLocation("vehicle:fluid_extractor")));
    public static final RegistryObject<RecipeType<FluidMixerRecipe>> FLUID_MIXER = REGISTER.register("fluid_mixer", () -> RecipeType.simple(new ResourceLocation("vehicle:fluid_mixer")));
    public static final RegistryObject<RecipeType<WorkstationRecipe>> WORKSTATION = REGISTER.register("workstation", () -> RecipeType.simple(new ResourceLocation("vehicle:workstation")));
}
