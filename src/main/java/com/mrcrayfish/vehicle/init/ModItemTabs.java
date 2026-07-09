/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItemTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"vehicle");
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = REGISTER.register("main_tab", () -> CreativeModeTab.builder().title((Component)Component.literal((String)"Vehicles")).icon(() -> ((Item)ModItems.IRON_SMALL_ENGINE.get()).getDefaultInstance()).displayItems((params, output) -> {
        output.accept((ItemLike)ModItems.PANEL.get());
        output.accept((ItemLike)ModItems.STANDARD_WHEEL.get());
        output.accept((ItemLike)ModItems.SPORTS_WHEEL.get());
        output.accept((ItemLike)ModItems.RACING_WHEEL.get());
        output.accept((ItemLike)ModItems.OFF_ROAD_WHEEL.get());
        output.accept((ItemLike)ModItems.SNOW_WHEEL.get());
        output.accept((ItemLike)ModItems.ALL_TERRAIN_WHEEL.get());
        output.accept((ItemLike)ModItems.PLASTIC_WHEEL.get());
        output.accept((ItemLike)ModItems.IRON_SMALL_ENGINE.get());
        output.accept((ItemLike)ModItems.GOLD_SMALL_ENGINE.get());
        output.accept((ItemLike)ModItems.DIAMOND_SMALL_ENGINE.get());
        output.accept((ItemLike)ModItems.NETHERITE_SMALL_ENGINE.get());
        output.accept((ItemLike)ModItems.IRON_LARGE_ENGINE.get());
        output.accept((ItemLike)ModItems.GOLD_LARGE_ENGINE.get());
        output.accept((ItemLike)ModItems.DIAMOND_LARGE_ENGINE.get());
        output.accept((ItemLike)ModItems.NETHERITE_LARGE_ENGINE.get());
        output.accept((ItemLike)ModItems.IRON_ELECTRIC_ENGINE.get());
        output.accept((ItemLike)ModItems.GOLD_ELECTRIC_ENGINE.get());
        output.accept((ItemLike)ModItems.DIAMOND_ELECTRIC_ENGINE.get());
        output.accept((ItemLike)ModItems.NETHERITE_ELECTRIC_ENGINE.get());
        ItemStack sprayCan = ((SprayCanItem)ModItems.SPRAY_CAN.get()).getDefaultInstance();
        ((SprayCanItem)ModItems.SPRAY_CAN.get()).refill(sprayCan);
        output.accept(sprayCan);
        output.accept((ItemLike)ModItems.JERRY_CAN.get());
        output.accept((ItemLike)ModItems.INDUSTRIAL_JERRY_CAN.get());
        output.accept((ItemLike)ModItems.WRENCH.get());
        output.accept((ItemLike)ModItems.HAMMER.get());
        output.accept((ItemLike)ModItems.KEY.get());
        output.accept((ItemLike)ModItems.FUELIUM_BUCKET.get());
        output.accept((ItemLike)ModItems.ENDER_SAP_BUCKET.get());
        output.accept((ItemLike)ModItems.BLAZE_JUICE_BUCKET.get());
        output.accept((ItemLike)ModBlocks.TRAFFIC_CONE.get());
        output.accept((ItemLike)ModBlocks.FLUID_EXTRACTOR.get());
        output.accept((ItemLike)ModBlocks.FLUID_MIXER.get());
        output.accept((ItemLike)ModBlocks.FLUID_PIPE.get());
        output.accept((ItemLike)ModBlocks.FLUID_PUMP.get());
        output.accept((ItemLike)ModBlocks.GAS_PUMP.get());
        output.accept((ItemLike)ModBlocks.FUEL_DRUM.get());
        output.accept((ItemLike)ModBlocks.INDUSTRIAL_FUEL_DRUM.get());
        output.accept((ItemLike)ModBlocks.WORKSTATION.get());
        output.accept((ItemLike)ModBlocks.JACK.get());
        VehicleCrateBlock.REGISTERED_CRATES.forEach(resourceLocation -> {
            CompoundTag blockEntityTag = new CompoundTag();
            blockEntityTag.putString("Vehicle", resourceLocation.toString());
            blockEntityTag.putBoolean("Creative", true);
            ItemStack stack = new ItemStack((ItemLike)ModBlocks.VEHICLE_CRATE.get());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of((CompoundTag)blockEntityTag));
            output.accept(stack);
        });
    }).build());
}

