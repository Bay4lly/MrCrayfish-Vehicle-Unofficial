package com.mrcrayfish.vehicle.crafting;

import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class FluidMixerRecipe implements Recipe<FluidMixerRecipe.FluidMixerInput>
{
    public static final com.mojang.serialization.MapCodec<FluidMixerRecipe> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
        com.mrcrayfish.vehicle.crafting.FluidEntry.CODEC.listOf().fieldOf("input").forGetter(recipe -> java.util.List.of(recipe.getInputs()[0], recipe.getInputs()[1])),
        net.minecraft.world.item.ItemStack.CODEC.fieldOf("ingredient").forGetter(FluidMixerRecipe::getIngredient),
        com.mrcrayfish.vehicle.crafting.FluidEntry.CODEC.fieldOf("result").forGetter(FluidMixerRecipe::getResult)
    ).apply(inst, (inputs, ingredient, result) -> new FluidMixerRecipe(inputs.get(0), inputs.get(1), ingredient, result)));

    public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, FluidMixerRecipe> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.of(
        (buf, recipe) -> {
            com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.encode(buf, recipe.getInputs()[0]);
            com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.encode(buf, recipe.getInputs()[1]);
            net.minecraft.world.item.ItemStack.STREAM_CODEC.encode(buf, recipe.getIngredient());
            com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.encode(buf, recipe.getResult());
        },
        buf -> {
            com.mrcrayfish.vehicle.crafting.FluidEntry fluidOne = com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.decode(buf);
            com.mrcrayfish.vehicle.crafting.FluidEntry fluidTwo = com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.decode(buf);
            net.minecraft.world.item.ItemStack ingredient = net.minecraft.world.item.ItemStack.STREAM_CODEC.decode(buf);
            com.mrcrayfish.vehicle.crafting.FluidEntry result = com.mrcrayfish.vehicle.crafting.FluidEntry.STREAM_CODEC.decode(buf);
            return new FluidMixerRecipe(fluidOne, fluidTwo, ingredient, result);
        }
    );

    private FluidEntry[] inputs;
    private ItemStack ingredient;
    private FluidEntry result;
    private int hashCode;

    public FluidMixerRecipe(FluidEntry fluidOne, FluidEntry fluidTwo, ItemStack ingredient, FluidEntry result)
    {
        this.inputs = new FluidEntry[]{fluidOne, fluidTwo};
        this.ingredient = ingredient;
        this.result = result;
    }

    public FluidEntry[] getInputs()
    {
        return inputs;
    }

    public ItemStack getIngredient()
    {
        return this.ingredient;
    }

    public FluidEntry getResult()
    {
        return result;
    }

    public int getFluidAmount(Fluid fluid)
    {
        for(int i = 0; i < 2; i++)
        {
            FluidEntry entry = this.inputs[i];
            if(entry.getFluid().equals(fluid))
            {
                return entry.getAmount();
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof FluidMixerRecipe)) return false;
        FluidMixerRecipe other = (FluidMixerRecipe) obj;
        int index = -1;
        for(int i = 0; i < 2; i++)
        {
            if(other.inputs[0].getFluid().equals(this.inputs[i].getFluid()))
            {
                index = i == 1 ? 0 : 1;
            }
        }
        if(index == -1) return false;
        if(!other.inputs[1].getFluid().equals(this.inputs[index].getFluid())) return false;
        return InventoryUtil.areItemStacksEqualIgnoreCount(other.ingredient, this.ingredient);
    }

    @Override
    public int hashCode()
    {
        if(this.hashCode == 0)
        {
            this.hashCode = Objects.hash(BuiltInRegistries.FLUID.getKey(this.inputs[0].getFluid()), BuiltInRegistries.FLUID.getKey(this.inputs[1].getFluid()), BuiltInRegistries.ITEM.getKey(this.ingredient.getItem()));
        }
        return this.hashCode;
    }

    @Override
    public boolean matches(FluidMixerInput input, Level worldIn)
    {
        if(input.fluidOne() == Fluids.EMPTY || input.fluidTwo() == Fluids.EMPTY)
            return false;
        Fluid inputOne = input.fluidOne();
        int index = -1;
        for(int i = 0; i < 2; i++)
        {
            if(inputOne.equals(this.inputs[i].getFluid()))
            {
                index = i == 1 ? 0 : 1;
            }
        }
        if(index == -1) return false;
        Fluid inputTwo = input.fluidTwo();
        if(!inputTwo.equals(this.inputs[index].getFluid())) return false;
        return InventoryUtil.areItemStacksEqualIgnoreCount(input.ingredient(), this.ingredient);
    }

    @Override
    public ItemStack assemble(FluidMixerInput inv, net.minecraft.core.HolderLookup.Provider registries)
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
        return ModRecipeSerializers.FLUID_MIXER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return ModRecipeTypes.FLUID_MIXER.get();
    }

    public record FluidMixerInput(ItemStack ingredient, Fluid fluidOne, Fluid fluidTwo) implements net.minecraft.world.item.crafting.RecipeInput
    {
        @Override
        public ItemStack getItem(int slot)
        {
            if (slot != 0) throw new IllegalArgumentException("No item for index " + slot);
            return this.ingredient;
        }

        @Override
        public int size()
        {
            return 1;
        }
    }
}