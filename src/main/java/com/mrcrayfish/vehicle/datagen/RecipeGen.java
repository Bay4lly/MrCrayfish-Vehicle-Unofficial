/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.RecipeProvider
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.SmithingTransformRecipeBuilder
 *  net.minecraft.data.recipes.SpecialRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.common.conditions.ICondition
 *  net.neoforged.neoforge.common.conditions.ModLoadedCondition
 */
package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.datagen.FluidExtractorRecipeBuilder;
import com.mrcrayfish.vehicle.datagen.FluidMixerRecipeBuilder;
import com.mrcrayfish.vehicle.datagen.WorkstationRecipeBuilder;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.recipe.RecipeColorSprayCan;
import com.mrcrayfish.vehicle.recipe.RecipeRefillSprayCan;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public class RecipeGen
extends RecipeProvider {
    public RecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generator, lookupProvider);
    }

    protected void buildRecipes(RecipeOutput consumer) {
        RecipeGen.netheriteSmithing(consumer, (Item)ModItems.DIAMOND_ELECTRIC_ENGINE.get(), (Item)ModItems.NETHERITE_ELECTRIC_ENGINE.get());
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.DIAMOND_ELECTRIC_ENGINE.get())).pattern(" U ").pattern("UEU").pattern(" U ").define(Character.valueOf('U'), Tags.Items.GEMS_DIAMOND).define(Character.valueOf('E'), (ItemLike)ModItems.GOLD_ELECTRIC_ENGINE.get()).unlockedBy("has_diamond", RecipeGen.has((TagKey)Tags.Items.GEMS_DIAMOND)).unlockedBy("has_gold_electric_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.GOLD_ELECTRIC_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.GOLD_ELECTRIC_ENGINE.get())).pattern(" U ").pattern("UEU").pattern(" U ").define(Character.valueOf('U'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('E'), (ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get()).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_iron_electric_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get())).pattern("IRI").pattern("TBT").pattern("IPI").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('R'), (ItemLike)Items.REPEATER).define(Character.valueOf('T'), (ItemLike)Items.REDSTONE_TORCH).define(Character.valueOf('P'), (ItemLike)ModItems.PANEL.get()).define(Character.valueOf('B'), (ItemLike)Items.REDSTONE_BLOCK).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_repeater", RecipeGen.has((ItemLike)Items.REPEATER)).unlockedBy("has_redstone_torch", RecipeGen.has((ItemLike)Items.REDSTONE_TORCH)).unlockedBy("has_panel", RecipeGen.has((ItemLike)((ItemLike)ModItems.PANEL.get()))).unlockedBy("has_redstone_block", RecipeGen.has((ItemLike)Items.REDSTONE_BLOCK)).save(consumer);
        RecipeGen.netheriteSmithing(consumer, (Item)ModItems.DIAMOND_SMALL_ENGINE.get(), (Item)ModItems.NETHERITE_SMALL_ENGINE.get());
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.DIAMOND_SMALL_ENGINE.get())).pattern(" U ").pattern("UEU").pattern(" U ").define(Character.valueOf('U'), Tags.Items.GEMS_DIAMOND).define(Character.valueOf('E'), (ItemLike)ModItems.GOLD_SMALL_ENGINE.get()).unlockedBy("has_diamond", RecipeGen.has((TagKey)Tags.Items.GEMS_DIAMOND)).unlockedBy("has_gold_small_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.GOLD_SMALL_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.GOLD_SMALL_ENGINE.get())).pattern(" U ").pattern("UEU").pattern(" U ").define(Character.valueOf('U'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('E'), (ItemLike)ModItems.IRON_SMALL_ENGINE.get()).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_iron_small_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.IRON_SMALL_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.IRON_SMALL_ENGINE.get())).pattern("IRI").pattern("PFP").pattern("IRI").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('R'), (ItemLike)Items.REPEATER).define(Character.valueOf('P'), (ItemLike)ModItems.PANEL.get()).define(Character.valueOf('F'), (ItemLike)Items.FURNACE).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_repeater", RecipeGen.has((ItemLike)Items.REPEATER)).unlockedBy("has_redstone_torch", RecipeGen.has((ItemLike)Items.REDSTONE_TORCH)).unlockedBy("has_panel", RecipeGen.has((ItemLike)((ItemLike)ModItems.PANEL.get()))).unlockedBy("has_furnace", RecipeGen.has((ItemLike)Items.FURNACE)).save(consumer);
        RecipeGen.netheriteSmithing(consumer, (Item)ModItems.DIAMOND_LARGE_ENGINE.get(), (Item)ModItems.NETHERITE_LARGE_ENGINE.get());
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.DIAMOND_LARGE_ENGINE.get())).pattern(" U ").pattern("UEU").pattern(" U ").define(Character.valueOf('U'), Tags.Items.GEMS_DIAMOND).define(Character.valueOf('E'), (ItemLike)ModItems.GOLD_LARGE_ENGINE.get()).unlockedBy("has_diamond", RecipeGen.has((TagKey)Tags.Items.GEMS_DIAMOND)).unlockedBy("has_gold_large_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.GOLD_LARGE_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.GOLD_LARGE_ENGINE.get())).pattern(" U ").pattern("UEU").pattern(" U ").define(Character.valueOf('U'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('E'), (ItemLike)ModItems.IRON_LARGE_ENGINE.get()).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_iron_large_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.IRON_LARGE_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.IRON_LARGE_ENGINE.get())).pattern("BRB").pattern("PFP").pattern("IRI").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE).define(Character.valueOf('P'), (ItemLike)ModItems.PANEL.get()).define(Character.valueOf('F'), (ItemLike)Items.FURNACE).define(Character.valueOf('B'), (ItemLike)Items.IRON_BLOCK).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_redstone", RecipeGen.has((ItemLike)Items.REDSTONE)).unlockedBy("has_panel", RecipeGen.has((ItemLike)((ItemLike)ModItems.PANEL.get()))).unlockedBy("has_furnace", RecipeGen.has((ItemLike)Items.FURNACE)).unlockedBy("has_iron_block", RecipeGen.has((ItemLike)Items.IRON_BLOCK)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.FLUID_EXTRACTOR.get())).pattern("III").pattern("GPR").pattern("IEI").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('G'), Tags.Items.GLASS_BLOCKS).define(Character.valueOf('P'), (ItemLike)Items.PISTON).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE_BLOCK).define(Character.valueOf('E'), (ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get()).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_glass", RecipeGen.has((TagKey)Tags.Items.GLASS_BLOCKS)).unlockedBy("has_piston", RecipeGen.has((ItemLike)Items.PISTON)).unlockedBy("has_redstone_block", RecipeGen.has((ItemLike)Items.REDSTONE_BLOCK)).unlockedBy("has_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.FLUID_MIXER.get())).pattern("III").pattern("HRH").pattern("IEI").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE_BLOCK).define(Character.valueOf('H'), (ItemLike)Items.HOPPER).define(Character.valueOf('E'), (ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get()).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_redstone_block", RecipeGen.has((ItemLike)Items.REDSTONE_BLOCK)).unlockedBy("has_hopper", RecipeGen.has((ItemLike)Items.HOPPER)).unlockedBy("has_engine", RecipeGen.has((ItemLike)((ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.FLUID_PIPE.get()), (int)8).pattern("IRI").pattern("GGG").pattern("III").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE).define(Character.valueOf('G'), Tags.Items.GLASS_PANES).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_redstone", RecipeGen.has((ItemLike)Items.REDSTONE)).unlockedBy("has_glass_pane", RecipeGen.has((TagKey)Tags.Items.GLASS_PANES)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.FLUID_PUMP.get()), (int)2).pattern("IRI").pattern("GDG").pattern("IHI").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE).define(Character.valueOf('G'), Tags.Items.GLASS_PANES).define(Character.valueOf('D'), (ItemLike)Items.DISPENSER).define(Character.valueOf('H'), (ItemLike)Items.HOPPER).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_redstone", RecipeGen.has((ItemLike)Items.REDSTONE)).unlockedBy("has_glass_pane", RecipeGen.has((TagKey)Tags.Items.GLASS_PANES)).unlockedBy("has_dispenser", RecipeGen.has((ItemLike)Items.DISPENSER)).unlockedBy("has_hopper", RecipeGen.has((ItemLike)Items.HOPPER)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.FUEL_DRUM.get())).pattern("III").pattern("PBP").pattern("III").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('B'), (ItemLike)Items.BUCKET).define(Character.valueOf('P'), (ItemLike)ModItems.PANEL.get()).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_bucket", RecipeGen.has((ItemLike)Items.BUCKET)).unlockedBy("has_panel", RecipeGen.has((ItemLike)((ItemLike)ModItems.PANEL.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.INDUSTRIAL_FUEL_DRUM.get())).pattern("III").pattern("IFI").pattern("III").define(Character.valueOf('I'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('F'), (ItemLike)ModBlocks.FUEL_DRUM.get()).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_fuel_drum", RecipeGen.has((ItemLike)((ItemLike)ModBlocks.FUEL_DRUM.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.GAS_PUMP.get())).pattern("IRI").pattern("GPG").pattern("IFI").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE_BLOCK).define(Character.valueOf('P'), (ItemLike)ModBlocks.FLUID_PUMP.get()).define(Character.valueOf('F'), (ItemLike)ModBlocks.FUEL_DRUM.get()).define(Character.valueOf('G'), Tags.Items.INGOTS_GOLD).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_redstone_block", RecipeGen.has((ItemLike)Items.REDSTONE_BLOCK)).unlockedBy("has_fluid_pump", RecipeGen.has((ItemLike)((ItemLike)ModBlocks.FLUID_PUMP.get()))).unlockedBy("has_fuel_drum", RecipeGen.has((ItemLike)((ItemLike)ModBlocks.FUEL_DRUM.get()))).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.HAMMER.get())).pattern("III").pattern(" G ").pattern(" W ").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('G'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('W'), (ItemLike)Items.BLACK_WOOL).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_black_wool", RecipeGen.has((ItemLike)Items.BLACK_WOOL)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.WRENCH.get())).pattern("I").pattern("G").pattern("W").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('G'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('W'), (ItemLike)Items.BLACK_WOOL).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_black_wool", RecipeGen.has((ItemLike)Items.BLACK_WOOL)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.JERRY_CAN.get())).pattern("III").pattern("IDI").pattern("III").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('D'), Tags.Items.DYES_PURPLE).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_purple_dye", RecipeGen.has((TagKey)Tags.Items.DYES_PURPLE)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.INDUSTRIAL_JERRY_CAN.get())).pattern("III").pattern("IJI").pattern("III").define(Character.valueOf('I'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('J'), (ItemLike)ModItems.JERRY_CAN.get()).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_jerry_can", RecipeGen.has((ItemLike)((ItemLike)ModItems.JERRY_CAN.get()))).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.JACK.get())).pattern("IPI").pattern("IRI").define(Character.valueOf('I'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('P'), (ItemLike)Items.PISTON).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_piston", RecipeGen.has((ItemLike)Items.PISTON)).unlockedBy("has_redstone", RecipeGen.has((ItemLike)Items.REDSTONE)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.KEY.get())).pattern("WII").define(Character.valueOf('W'), (ItemLike)Items.BLACK_WOOL).define(Character.valueOf('I'), Tags.Items.INGOTS_GOLD).unlockedBy("has_black_wool", RecipeGen.has((ItemLike)Items.BLACK_WOOL)).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.PANEL.get()), (int)2).pattern("III").pattern("III").define(Character.valueOf('I'), Tags.Items.NUGGETS_IRON).unlockedBy("has_iron_nugget", RecipeGen.has((TagKey)Tags.Items.NUGGETS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.TRAFFIC_CONE.get()), (int)8).pattern("O").pattern("W").pattern("O").define(Character.valueOf('O'), (ItemLike)Items.ORANGE_CONCRETE).define(Character.valueOf('W'), (ItemLike)Items.WHITE_CONCRETE).unlockedBy("has_orange_concrete", RecipeGen.has((ItemLike)Items.ORANGE_CONCRETE)).unlockedBy("has_white_concrete", RecipeGen.has((ItemLike)Items.WHITE_CONCRETE)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModBlocks.WORKSTATION.get())).pattern("III").pattern("GCG").pattern("GGG").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('G'), Tags.Items.INGOTS_GOLD).define(Character.valueOf('C'), (ItemLike)Items.CRAFTING_TABLE).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_gold_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_GOLD)).unlockedBy("has_crafting_table", RecipeGen.has((ItemLike)Items.CRAFTING_TABLE)).save(consumer);
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.SPRAY_CAN.get())).pattern("IDI").pattern("IWI").pattern("III").define(Character.valueOf('I'), Tags.Items.INGOTS_IRON).define(Character.valueOf('D'), Tags.Items.DYES_WHITE).define(Character.valueOf('W'), (ItemLike)Items.BUCKET).unlockedBy("has_iron_ingot", RecipeGen.has((TagKey)Tags.Items.INGOTS_IRON)).unlockedBy("has_white_dye", RecipeGen.has((TagKey)Tags.Items.DYES_WHITE)).unlockedBy("has_bucket", RecipeGen.has((ItemLike)Items.BUCKET)).save(consumer);
        SpecialRecipeBuilder.special(RecipeColorSprayCan::new).save(consumer, ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"color_spray_can"));
        SpecialRecipeBuilder.special(RecipeRefillSprayCan::new).save(consumer, ResourceLocation.fromNamespaceAndPath((String)"vehicle", (String)"refill_spray_can"));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ALUMINUM_BOAT.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 10));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ATV.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of((ItemLike)Items.IRON_BARS, 4), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 4), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 6), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 8));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.BUMPER_CAR.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 8), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 8));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.DIRT_BIKE.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 32), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 2), WorkstationIngredient.of((ItemLike)Items.GRAY_WOOL, 2));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.DUNE_BUGGY.get()), WorkstationIngredient.of((ItemLike)Items.YELLOW_CONCRETE, 8), WorkstationIngredient.of((ItemLike)Items.BLUE_CONCRETE, 4), WorkstationIngredient.of((ItemLike)Items.RED_CONCRETE, 2));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.GO_KART.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 4));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.GOLF_CART.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of((ItemLike)Items.IRON_BARS, 4), WorkstationIngredient.of((ItemLike)Items.WHITE_WOOL, 8), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 12), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 16));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.JET_SKI.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 64), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 10));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.LAWN_MOWER.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 4), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 8));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.MINI_BIKE.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 24), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 2));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.MINI_BUS.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 128), WorkstationIngredient.of((ItemLike)Items.GRAY_WOOL, 5), WorkstationIngredient.of((TagKey<Item>)Tags.Items.GLASS_PANES, 9), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 12), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 16));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.MOPED.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of((ItemLike)Items.IRON_BARS, 2), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 4), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 6));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.OFF_ROADER.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 128), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 8), WorkstationIngredient.of((TagKey<Item>)Tags.Items.GLASS_PANES, 6), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 12), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 24));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SHOPPING_CART.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 8), WorkstationIngredient.of((ItemLike)Items.IRON_BARS, 4));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SMART_CAR.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 8), WorkstationIngredient.of((TagKey<Item>)Tags.Items.GLASS_PANES, 6), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 8), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 16));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SPEED_BOAT.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 8), WorkstationIngredient.of((TagKey<Item>)Tags.Items.GLASS_PANES, 4), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 10));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SPORTS_PLANE.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 180), WorkstationIngredient.of((TagKey<Item>)Tags.Items.GLASS_PANES, 16), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 18), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 32));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.TRACTOR.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 128), WorkstationIngredient.of((ItemLike)Items.BLACK_WOOL, 4), WorkstationIngredient.of((ItemLike)Items.REDSTONE, 8), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 16));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.FERTILIZER.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 8));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.FLUID_TRAILER.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 8));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SEEDER.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 42), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 8));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.STORAGE_TRAILER.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 2), WorkstationIngredient.of((ItemLike)Items.CHEST, 1));
        RecipeGen.workstationCrafting(consumer, (EntityType<? extends VehicleEntity>)((EntityType)ModEntities.VEHICLE_TRAILER.get()), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of((ItemLike)ModItems.PANEL.get(), 2));
        RecipeGen.dependantWorkstationCrafting(consumer, "cfm", ResourceLocation.parse((String)"vehicle:sofa"), WorkstationIngredient.of(ResourceLocation.parse((String)"cfm:rainbow_sofa"), 1), WorkstationIngredient.of((TagKey<Item>)Tags.Items.INGOTS_IRON, 8));
        RecipeGen.fluidExtracting(consumer, (ItemLike)Items.BLAZE_ROD, FluidEntry.of((Fluid)ModFluids.BLAZE_JUICE.get(), 450));
        RecipeGen.fluidExtracting(consumer, (ItemLike)Items.ENDER_PEARL, FluidEntry.of((Fluid)ModFluids.ENDER_SAP.get(), 600));
        RecipeGen.fluidMixing(consumer, FluidEntry.of((Fluid)ModFluids.ENDER_SAP.get(), 200), FluidEntry.of((Fluid)ModFluids.BLAZE_JUICE.get(), 200), (ItemLike)Items.GLOWSTONE_DUST, FluidEntry.of((Fluid)ModFluids.FUELIUM.get(), 400));
    }

    private static void netheriteSmithing(RecipeOutput consumer, Item inputItem, Item resultItem) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(resultItem.asItem());
        SmithingTransformRecipeBuilder.smithing((Ingredient)Ingredient.of((ItemLike[])new ItemLike[]{Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE}), (Ingredient)Ingredient.of((ItemLike[])new ItemLike[]{inputItem}), (Ingredient)Ingredient.of((ItemLike[])new ItemLike[]{Items.NETHERITE_INGOT}), (RecipeCategory)RecipeCategory.MISC, (Item)resultItem).unlocks("has_netherite_ingot", RecipeGen.has((ItemLike)Items.NETHERITE_INGOT)).save(consumer, ResourceLocation.fromNamespaceAndPath((String)id.getNamespace(), (String)(id.getPath() + "_smithing")));
    }

    private static void workstationCrafting(RecipeOutput consumer, EntityType<? extends VehicleEntity> type, WorkstationIngredient ... materials) {
        ResourceLocation entityId = Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(type));
        WorkstationRecipeBuilder.crafting(entityId, Arrays.asList(materials)).save(consumer, ResourceLocation.fromNamespaceAndPath((String)entityId.getNamespace(), (String)(entityId.getPath() + "_crafting")));
    }

    private static void dependantWorkstationCrafting(RecipeOutput consumer, String modId, ResourceLocation entityId, WorkstationIngredient ... materials) {
        WorkstationRecipeBuilder.crafting(entityId, Arrays.asList(materials)).addCondition((ICondition)new ModLoadedCondition(modId)).save(consumer, ResourceLocation.fromNamespaceAndPath((String)entityId.getNamespace(), (String)(entityId.getPath() + "_crafting")));
    }

    private static void fluidExtracting(RecipeOutput consumer, ItemLike provider, FluidEntry output) {
        ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(output.getFluid()));
        FluidExtractorRecipeBuilder.extracting(Ingredient.of((ItemLike[])new ItemLike[]{provider}), output).save(consumer, ResourceLocation.fromNamespaceAndPath((String)id.getNamespace(), (String)(id.getPath() + "_extracting")));
    }

    private static void fluidMixing(RecipeOutput consumer, FluidEntry inputOne, FluidEntry inputTwo, ItemLike provider, FluidEntry output) {
        ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(output.getFluid()));
        FluidMixerRecipeBuilder.mixing(inputOne, inputTwo, Ingredient.of((ItemLike[])new ItemLike[]{provider}), output).save(consumer, ResourceLocation.fromNamespaceAndPath((String)id.getNamespace(), (String)(id.getPath() + "_mixing")));
    }
}

