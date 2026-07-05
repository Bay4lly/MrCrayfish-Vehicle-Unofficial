package com.mrcrayfish.vehicle.crafting;

import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class FluidExtractorRecipe implements Recipe<net.minecraft.world.item.crafting.SingleRecipeInput>
{
    public static final com.mojang.serialization.MapCodec<FluidExtractorRecipe> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
        net.minecraft.world.item.ItemStack.CODEC.fieldOf("ingredient").forGetter(FluidExtractorRecipe::getIngredient),
        com.mrcrayfish.vehicle.crafting.FluidEntry.CODEC.fieldOf("result").forGetter(FluidExtractorRecipe::getResult)
    ).apply(inst, FluidExtractorRecipe::new));

    public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, FluidExtractorRecipe> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.of(
        (buf, recipe) -> {
            net.minecraft.world.item.ItemStack.STREAM_CODEC.encode(buf, recipe.getIngredient());
            com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.encode(buf, recipe.getResult());
        },
        buf -> {
            net.minecraft.world.item.ItemStack ingredient = net.minecraft.world.item.ItemStack.STREAM_CODEC.decode(buf);
            com.mrcrayfish.vehicle.crafting.FluidEntry result = com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.decode(buf);
            return new FluidExtractorRecipe(ingredient, result);
        }
    );

    private ItemStack ingredient;
    private FluidEntry result;

    public FluidExtractorRecipe(ItemStack ingredient, FluidEntry result)
    {
        this.ingredient = ingredient;
        this.result = result;
    }

    public ItemStack getIngredient()
    {
        return ingredient;
    }

    public FluidEntry getResult()
    {
        return result;
    }

    @Override
    public boolean matches(net.minecraft.world.item.crafting.SingleRecipeInput input, Level worldIn)
    {
        ItemStack source = input.getItem(0);
        return InventoryUtil.areItemStacksEqualIgnoreCount(source, this.ingredient);
    }

    @Override
    public ItemStack assemble(net.minecraft.world.item.crafting.SingleRecipeInput inv, net.minecraft.core.HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.FLUID_EXTRACTOR.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return ModRecipeTypes.FLUID_EXTRACTOR.get();
    }
}
