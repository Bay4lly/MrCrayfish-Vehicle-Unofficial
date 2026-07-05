package com.mrcrayfish.vehicle.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Author: MrCrayfish
 */
public class WorkstationRecipeSerializer implements RecipeSerializer<WorkstationRecipe>
{
    @Override
    public MapCodec<WorkstationRecipe> codec()
    {
        return WorkstationRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WorkstationRecipe> streamCodec()
    {
        return WorkstationRecipe.STREAM_CODEC;
    }
}
