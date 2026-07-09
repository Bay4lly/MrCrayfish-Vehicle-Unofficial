/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
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
import java.util.List;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidMixerRecipe
implements Recipe<FluidMixerRecipe.FluidMixerInput> {
    public static final MapCodec<FluidMixerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(FluidEntry.CODEC.listOf().fieldOf("input").forGetter(recipe -> List.of(recipe.getInputs()[0], recipe.getInputs()[1])), ItemStack.CODEC.fieldOf("ingredient").forGetter(FluidMixerRecipe::getIngredient), FluidEntry.CODEC.fieldOf("result").forGetter(FluidMixerRecipe::getResult)).apply(inst, (inputs, ingredient, result) -> new FluidMixerRecipe(inputs.get(0), inputs.get(1), ingredient, result)));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidMixerRecipe> STREAM_CODEC = StreamCodec.of((buf, recipe) -> {
        FluidEntry.STREAM_CODEC.encode(buf, recipe.getInputs()[0]);
        FluidEntry.STREAM_CODEC.encode(buf, recipe.getInputs()[1]);
        ItemStack.STREAM_CODEC.encode(buf, recipe.getIngredient());
        FluidEntry.STREAM_CODEC.encode(buf, recipe.getResult());
    }, buf -> {
        FluidEntry fluidOne = (FluidEntry)FluidEntry.STREAM_CODEC.decode(buf);
        FluidEntry fluidTwo = (FluidEntry)FluidEntry.STREAM_CODEC.decode(buf);
        ItemStack ingredient = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
        FluidEntry result = (FluidEntry)FluidEntry.STREAM_CODEC.decode(buf);
        return new FluidMixerRecipe(fluidOne, fluidTwo, ingredient, result);
    });
    private FluidEntry[] inputs;
    private ItemStack ingredient;
    private FluidEntry result;
    private int hashCode;

    public FluidMixerRecipe(FluidEntry fluidOne, FluidEntry fluidTwo, ItemStack ingredient, FluidEntry result) {
        this.inputs = new FluidEntry[]{fluidOne, fluidTwo};
        this.ingredient = ingredient;
        this.result = result;
    }

    public FluidEntry[] getInputs() {
        return this.inputs;
    }

    public ItemStack getIngredient() {
        return this.ingredient;
    }

    public FluidEntry getResult() {
        return this.result;
    }

    public int getFluidAmount(Fluid fluid) {
        for (int i = 0; i < 2; ++i) {
            FluidEntry entry = this.inputs[i];
            if (!entry.getFluid().equals(fluid)) continue;
            return entry.getAmount();
        }
        return -1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FluidMixerRecipe)) {
            return false;
        }
        FluidMixerRecipe other = (FluidMixerRecipe)obj;
        int index = -1;
        for (int i = 0; i < 2; ++i) {
            if (!other.inputs[0].getFluid().equals(this.inputs[i].getFluid())) continue;
            index = i == 1 ? 0 : 1;
        }
        if (index == -1) {
            return false;
        }
        if (!other.inputs[1].getFluid().equals(this.inputs[index].getFluid())) {
            return false;
        }
        return InventoryUtil.areItemStacksEqualIgnoreCount(other.ingredient, this.ingredient);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = Objects.hash(BuiltInRegistries.FLUID.getKey(this.inputs[0].getFluid()), BuiltInRegistries.FLUID.getKey(this.inputs[1].getFluid()), BuiltInRegistries.ITEM.getKey(this.ingredient.getItem()));
        }
        return this.hashCode;
    }

    public boolean matches(FluidMixerInput input, Level worldIn) {
        if (input.fluidOne() == Fluids.EMPTY || input.fluidTwo() == Fluids.EMPTY) {
            return false;
        }
        Fluid inputOne = input.fluidOne();
        int index = -1;
        for (int i = 0; i < 2; ++i) {
            if (!inputOne.equals(this.inputs[i].getFluid())) continue;
            index = i == 1 ? 0 : 1;
        }
        if (index == -1) {
            return false;
        }
        Fluid inputTwo = input.fluidTwo();
        if (!inputTwo.equals(this.inputs[index].getFluid())) {
            return false;
        }
        return InventoryUtil.areItemStacksEqualIgnoreCount(input.ingredient(), this.ingredient);
    }

    public ItemStack assemble(FluidMixerInput inv, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer)ModRecipeSerializers.FLUID_MIXER.get();
    }

    public RecipeType<?> getType() {
        return (RecipeType)ModRecipeTypes.FLUID_MIXER.get();
    }

    public record FluidMixerInput(ItemStack ingredient, Fluid fluidOne, Fluid fluidTwo) implements RecipeInput
    {
        public ItemStack getItem(int slot) {
            if (slot != 0) {
                throw new IllegalArgumentException("No item for index " + slot);
            }
            return this.ingredient;
        }

        public int size() {
            return 1;
        }
    }
}

