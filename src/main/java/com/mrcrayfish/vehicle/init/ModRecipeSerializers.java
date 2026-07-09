/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipeSerializer;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipeSerializer;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipeSerializer;
import com.mrcrayfish.vehicle.recipe.RecipeColorSprayCan;
import com.mrcrayfish.vehicle.recipe.RecipeRefillSprayCan;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.RECIPE_SERIALIZER, (String)"vehicle");
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<RecipeColorSprayCan>> COLOR_SPRAY_CAN = REGISTER.register("color_spray_can", () -> new SimpleCraftingRecipeSerializer(RecipeColorSprayCan::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<RecipeRefillSprayCan>> REFILL_SPRAY_CAN = REGISTER.register("refill_spray_can", () -> new SimpleCraftingRecipeSerializer(RecipeRefillSprayCan::new));
    public static final DeferredHolder<RecipeSerializer<?>, FluidExtractorRecipeSerializer> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", FluidExtractorRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, FluidMixerRecipeSerializer> FLUID_MIXER = REGISTER.register("fluid_mixer", FluidMixerRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, WorkstationRecipeSerializer> WORKSTATION = REGISTER.register("workstation", WorkstationRecipeSerializer::new);
}

