/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 */
package com.mrcrayfish.vehicle.crafting;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class FluidExtractorRecipe
implements Recipe<SingleRecipeInput> {
    public static final MapCodec<FluidExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(ItemStack.CODEC.fieldOf("ingredient").forGetter(FluidExtractorRecipe::getIngredient), FluidEntry.CODEC.fieldOf("result").forGetter(FluidExtractorRecipe::getResult)).apply(inst, FluidExtractorRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidExtractorRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {
        ItemStack.STREAM_CODEC.encode(buf, recipe.getIngredient());
        FluidEntry.STREAM_CODEC.encode(buf, recipe.getResult());
    }, buf -> {
        ItemStack ingredient = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
        FluidEntry result = (FluidEntry)FluidEntry.STREAM_CODEC.decode(buf);
        return new FluidExtractorRecipe(ingredient, result);
    });
    private ItemStack ingredient;
    private FluidEntry result;

    public FluidExtractorRecipe(ItemStack ingredient, FluidEntry result) {
        this.ingredient = ingredient;
        this.result = result;
    }

    public ItemStack getIngredient() {
        return this.ingredient;
    }

    public FluidEntry getResult() {
        return this.result;
    }

    public boolean matches(SingleRecipeInput input, Level worldIn) {
        ItemStack source = input.getItem(0);
        return InventoryUtil.areItemStacksEqualIgnoreCount(source, this.ingredient);
    }

    public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer)ModRecipeSerializers.FLUID_EXTRACTOR.get();
    }

    public RecipeType<?> getType() {
        return (RecipeType)ModRecipeTypes.FLUID_EXTRACTOR.get();
    }
}

