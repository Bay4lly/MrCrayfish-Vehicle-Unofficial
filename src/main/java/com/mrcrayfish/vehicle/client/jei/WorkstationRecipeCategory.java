package com.mrcrayfish.vehicle.client.jei;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
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
import net.minecraft.core.registries.BuiltInRegistries;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import net.minecraft.client.Minecraft;

import java.util.Arrays;
import java.util.List;

public class WorkstationRecipeCategory implements IRecipeCategory<WorkstationRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public WorkstationRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(140, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.WORKSTATION.get()));
    }

    @Override
    public RecipeType<WorkstationRecipe> getRecipeType() {
        return VehicleJeiPlugin.WORKSTATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.vehicle.workstation");
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
    public void setRecipe(IRecipeLayoutBuilder builder, WorkstationRecipe recipe, IFocusGroup focuses) {
        List<WorkstationIngredient> materials = recipe.getMaterials();
        int maxIndex = Math.min(materials.size(), 9);

        for (int i = 0; i < maxIndex; i++) {
            int x = 5 + (i % 3) * 18;
            int y = 5 + (i / 3) * 18;
            WorkstationIngredient ingredient = materials.get(i);
            
            ItemStack[] items = Arrays.stream(ingredient.getIngredient().getItems()).map(stack -> {
                ItemStack copy = stack.copy();
                copy.setCount(ingredient.getCount());
                return copy;
            }).toArray(ItemStack[]::new);

            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                   .addItemStacks(Arrays.asList(items));
        }

        if (recipe.getVehicle() != null) {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(recipe.getVehicle());
            ItemStack result = VehicleCrateBlock.create(
                Minecraft.getInstance().level.registryAccess(),
                entityId,
                -1, // default color
                ItemStack.EMPTY,
                ItemStack.EMPTY
            );
            builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 22)
                   .addItemStack(result);
        }
    }
}
