/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.RECIPE_TYPE, (String)"vehicle");
    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidExtractorRecipe>> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", () -> RecipeType.simple((ResourceLocation)ResourceLocation.parse((String)"vehicle:fluid_extractor")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidMixerRecipe>> FLUID_MIXER = REGISTER.register("fluid_mixer", () -> RecipeType.simple((ResourceLocation)ResourceLocation.parse((String)"vehicle:fluid_mixer")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<WorkstationRecipe>> WORKSTATION = REGISTER.register("workstation", () -> RecipeType.simple((ResourceLocation)ResourceLocation.parse((String)"vehicle:workstation")));
}

