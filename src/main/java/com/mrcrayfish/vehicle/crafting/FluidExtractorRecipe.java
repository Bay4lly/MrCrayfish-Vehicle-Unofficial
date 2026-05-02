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
public class FluidExtractorRecipe implements Recipe<FluidExtractorTileEntity>
{
    private ResourceLocation id;
    private ItemStack ingredient;
    private FluidEntry result;

    public FluidExtractorRecipe(ResourceLocation id, ItemStack ingredient, FluidEntry result)
    {
        this.id = id;
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
    public boolean matches(FluidExtractorTileEntity fluidExtractor, Level worldIn)
    {
        ItemStack source = fluidExtractor.getItem(FluidExtractorTileEntity.SLOT_FLUID_SOURCE);
        return InventoryUtil.areItemStacksEqualIgnoreCount(source, this.ingredient);
    }

    @Override
    public ItemStack assemble(FluidExtractorTileEntity inv, RegistryAccess registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
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
