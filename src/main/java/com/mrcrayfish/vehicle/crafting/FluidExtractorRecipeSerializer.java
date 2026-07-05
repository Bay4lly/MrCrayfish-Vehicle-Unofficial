package com.mrcrayfish.vehicle.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRecipeSerializer implements RecipeSerializer<FluidExtractorRecipe>
{
    @Override
    public MapCodec<FluidExtractorRecipe> codec()
    {
        return FluidExtractorRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FluidExtractorRecipe> streamCodec()
    {
        return FluidExtractorRecipe.STREAM_CODEC;
    }
}
