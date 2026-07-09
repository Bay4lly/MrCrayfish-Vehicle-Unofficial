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
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class WorkstationRecipeSerializer
implements RecipeSerializer<WorkstationRecipe> {
    public MapCodec<WorkstationRecipe> codec() {
        return WorkstationRecipe.CODEC;
    }

    public StreamCodec<RegistryFriendlyByteBuf, WorkstationRecipe> streamCodec() {
        return WorkstationRecipe.STREAM_CODEC;
    }
}

