package com.mrcrayfish.vehicle.client.jei;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
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

public class FluidMixerRecipeCategory implements IRecipeCategory<FluidMixerRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public FluidMixerRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation guiLocation = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/fluid_mixer.png");
        this.background = guiHelper.createDrawable(guiLocation, 20, 10, 140, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FLUID_MIXER.get()));
    }

    @Override
    public RecipeType<FluidMixerRecipe> getRecipeType() {
        return VehicleJeiPlugin.FLUID_MIXER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.vehicle.fluid_mixer");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FluidMixerRecipe recipe, IFocusGroup focuses) {
        if (recipe.getInputs().length >= 1 && recipe.getInputs()[0].getFluid() != null) {
            FluidEntry input1 = recipe.getInputs()[0];
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 7)
                   .addFluidStack(input1.getFluid(), input1.getAmount())
                   .setFluidRenderer(input1.getAmount(), false, 16, 29);
        }
        
        if (recipe.getInputs().length >= 2 && recipe.getInputs()[1].getFluid() != null) {
            FluidEntry input2 = recipe.getInputs()[1];
            builder.addSlot(RecipeIngredientRole.INPUT, 28, 7)
                   .addFluidStack(input2.getFluid(), input2.getAmount())
                   .setFluidRenderer(input2.getAmount(), false, 16, 29);
        }

        if (recipe.getIngredient() != null && !recipe.getIngredient().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 83, 31)
                   .addItemStack(recipe.getIngredient());
        }

        if (recipe.getResult() != null && recipe.getResult().getFluid() != null) {
            FluidEntry output = recipe.getResult();
            builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 7)
                   .addFluidStack(output.getFluid(), output.getAmount())
                   .setFluidRenderer(output.getAmount(), false, 16, 59);
        }
    }
}
