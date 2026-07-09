/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package com.mrcrayfish.vehicle.crafting;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FluidExtractorRecipeSerializer
implements RecipeSerializer<FluidExtractorRecipe> {
    public MapCodec<FluidExtractorRecipe> codec() {
        return FluidExtractorRecipe.CODEC;
    }

    public StreamCodec<RegistryFriendlyByteBuf, FluidExtractorRecipe> streamCodec() {
        return FluidExtractorRecipe.STREAM_CODEC;
    }
}

