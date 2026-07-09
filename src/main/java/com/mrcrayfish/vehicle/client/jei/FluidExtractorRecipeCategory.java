package com.mrcrayfish.vehicle.client.jei;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import com.mrcrayfish.vehicle.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidExtractorRecipeCategory implements IRecipeCategory<FluidExtractorRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public FluidExtractorRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation guiLocation = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/fluid_extractor.png");
        this.background = guiHelper.createDrawable(guiLocation, 55, 16, 90, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FLUID_EXTRACTOR.get()));
    }

    @Override
    public RecipeType<FluidExtractorRecipe> getRecipeType() {
        return VehicleJeiPlugin.FLUID_EXTRACTOR;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.vehicle.fluid_extractor");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidExtractorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 17)
               .addItemStack(recipe.getIngredient());

        if (recipe.getResult() != null && recipe.getResult().getFluid() != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 69, 1)
                   .addFluidStack(recipe.getResult().getFluid(), recipe.getResult().getAmount())
                   .setFluidRenderer(recipe.getResult().getAmount(), false, 16, 59);
        }
    }
}
