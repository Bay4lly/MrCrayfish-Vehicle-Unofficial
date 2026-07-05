package com.mrcrayfish.vehicle.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Author: MrCrayfish
 */
public class FluidMixerRecipeSerializer implements RecipeSerializer<FluidMixerRecipe>
{
    @Override
    public MapCodec<FluidMixerRecipe> codec()
    {
        return FluidMixerRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FluidMixerRecipe> streamCodec()
    {
        return FluidMixerRecipe.STREAM_CODEC;
    }
}
