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
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FluidMixerRecipeSerializer
implements RecipeSerializer<FluidMixerRecipe> {
    public MapCodec<FluidMixerRecipe> codec() {
        return FluidMixerRecipe.CODEC;
    }

    public StreamCodec<RegistryFriendlyByteBuf, FluidMixerRecipe> streamCodec() {
        return FluidMixerRecipe.STREAM_CODEC;
    }
}

