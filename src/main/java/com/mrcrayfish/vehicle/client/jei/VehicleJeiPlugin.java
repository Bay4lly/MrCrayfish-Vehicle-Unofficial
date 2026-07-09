package com.mrcrayfish.vehicle.client.jei;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.WorkstationRecipe;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class VehicleJeiPlugin implements IModPlugin {

    public static final RecipeType<WorkstationRecipe> WORKSTATION = RecipeType.create(Reference.MOD_ID, "workstation", WorkstationRecipe.class);
    public static final RecipeType<FluidMixerRecipe> FLUID_MIXER = RecipeType.create(Reference.MOD_ID, "fluid_mixer", FluidMixerRecipe.class);
    public static final RecipeType<FluidExtractorRecipe> FLUID_EXTRACTOR = RecipeType.create(Reference.MOD_ID, "fluid_extractor", FluidExtractorRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(
            new WorkstationRecipeCategory(guiHelper),
            new FluidMixerRecipeCategory(guiHelper),
            new FluidExtractorRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) return;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<WorkstationRecipe> workstationRecipes = new ArrayList<>();
        recipeManager.getAllRecipesFor(ModRecipeTypes.WORKSTATION.get()).forEach(holder -> workstationRecipes.add(holder.value()));
        registration.addRecipes(WORKSTATION, workstationRecipes);

        List<FluidMixerRecipe> mixerRecipes = new ArrayList<>();
        recipeManager.getAllRecipesFor(ModRecipeTypes.FLUID_MIXER.get()).forEach(holder -> mixerRecipes.add(holder.value()));
        registration.addRecipes(FLUID_MIXER, mixerRecipes);

        List<FluidExtractorRecipe> extractorRecipes = new ArrayList<>();
        recipeManager.getAllRecipesFor(ModRecipeTypes.FLUID_EXTRACTOR.get()).forEach(holder -> extractorRecipes.add(holder.value()));
        registration.addRecipes(FLUID_EXTRACTOR, extractorRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WORKSTATION.get()), WORKSTATION);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FLUID_MIXER.get()), FLUID_MIXER);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FLUID_EXTRACTOR.get()), FLUID_EXTRACTOR);
    }
}
