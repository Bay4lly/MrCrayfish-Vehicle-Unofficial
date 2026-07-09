/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.common.ModConfigSpec$IntValue
 *  net.neoforged.neoforge.registries.DeferredItem
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.DeferredRegister$Items
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.FluidPipeItem;
import com.mrcrayfish.vehicle.item.HammerItem;
import com.mrcrayfish.vehicle.item.ItemTrafficCone;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.KeyItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.item.WrenchItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems((String)"vehicle");
    public static final DeferredItem<Item> TRAFFIC_CONE = REGISTER.register("traffic_cone", () -> new ItemTrafficCone((Block)ModBlocks.TRAFFIC_CONE.get()));
    public static final DeferredItem<Item> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", () -> new BlockItem((Block)ModBlocks.FLUID_EXTRACTOR.get(), new Item.Properties()));
    public static final DeferredItem<Item> FLUID_MIXER = REGISTER.register("fluid_mixer", () -> new BlockItem((Block)ModBlocks.FLUID_MIXER.get(), new Item.Properties()));
    public static final DeferredItem<Item> GAS_PUMP = REGISTER.register("gas_pump", () -> new BlockItem((Block)ModBlocks.GAS_PUMP.get(), new Item.Properties()));
    public static final DeferredItem<Item> FLUID_PIPE = REGISTER.register("fluid_pipe", () -> new FluidPipeItem((Block)ModBlocks.FLUID_PIPE.get()));
    public static final DeferredItem<Item> FLUID_PUMP = REGISTER.register("fluid_pump", () -> new FluidPipeItem((Block)ModBlocks.FLUID_PUMP.get()));
    public static final DeferredItem<Item> FUEL_DRUM = REGISTER.register("fuel_drum", () -> new BlockItem((Block)ModBlocks.FUEL_DRUM.get(), new Item.Properties()));
    public static final DeferredItem<Item> INDUSTRIAL_FUEL_DRUM = REGISTER.register("industrial_fuel_drum", () -> new BlockItem((Block)ModBlocks.INDUSTRIAL_FUEL_DRUM.get(), new Item.Properties()));
    public static final DeferredItem<Item> WORKSTATION = REGISTER.register("workstation", () -> new BlockItem((Block)ModBlocks.WORKSTATION.get(), new Item.Properties()));
    public static final DeferredItem<Item> VEHICLE_CRATE = REGISTER.register("vehicle_crate", () -> new BlockItem((Block)ModBlocks.VEHICLE_CRATE.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> JACK = REGISTER.register("jack", () -> new BlockItem((Block)ModBlocks.JACK.get(), new Item.Properties()));
    public static final DeferredItem<Item> PANEL = REGISTER.register("panel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STANDARD_WHEEL = REGISTER.register("standard_wheel", () -> new WheelItem(WheelType.STANDARD, new Item.Properties()).setColored());
    public static final DeferredItem<Item> SPORTS_WHEEL = REGISTER.register("sports_wheel", () -> new WheelItem(WheelType.SPORTS, new Item.Properties()).setColored());
    public static final DeferredItem<Item> RACING_WHEEL = REGISTER.register("racing_wheel", () -> new WheelItem(WheelType.RACING, new Item.Properties()).setColored());
    public static final DeferredItem<Item> OFF_ROAD_WHEEL = REGISTER.register("off_road_wheel", () -> new WheelItem(WheelType.OFF_ROAD, new Item.Properties()).setColored());
    public static final DeferredItem<Item> SNOW_WHEEL = REGISTER.register("snow_wheel", () -> new WheelItem(WheelType.SNOW, new Item.Properties()).setColored());
    public static final DeferredItem<Item> ALL_TERRAIN_WHEEL = REGISTER.register("all_terrain_wheel", () -> new WheelItem(WheelType.ALL_TERRAIN, new Item.Properties()).setColored());
    public static final DeferredItem<Item> PLASTIC_WHEEL = REGISTER.register("plastic_wheel", () -> new WheelItem(WheelType.PLASTIC, new Item.Properties()));
    public static final DeferredItem<Item> IRON_SMALL_ENGINE = REGISTER.register("iron_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.IRON, new Item.Properties()));
    public static final DeferredItem<Item> GOLD_SMALL_ENGINE = REGISTER.register("gold_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.GOLD, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_SMALL_ENGINE = REGISTER.register("diamond_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_SMALL_ENGINE = REGISTER.register("netherite_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.NETHERITE, new Item.Properties()));
    public static final DeferredItem<Item> IRON_LARGE_ENGINE = REGISTER.register("iron_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.IRON, new Item.Properties()));
    public static final DeferredItem<Item> GOLD_LARGE_ENGINE = REGISTER.register("gold_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.GOLD, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_LARGE_ENGINE = REGISTER.register("diamond_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_LARGE_ENGINE = REGISTER.register("netherite_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.NETHERITE, new Item.Properties()));
    public static final DeferredItem<Item> IRON_ELECTRIC_ENGINE = REGISTER.register("iron_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.IRON, new Item.Properties()));
    public static final DeferredItem<Item> GOLD_ELECTRIC_ENGINE = REGISTER.register("gold_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.GOLD, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_ELECTRIC_ENGINE = REGISTER.register("diamond_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_ELECTRIC_ENGINE = REGISTER.register("netherite_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.NETHERITE, new Item.Properties()));
    public static final DeferredItem<SprayCanItem> SPRAY_CAN = REGISTER.register("spray_can", () -> new SprayCanItem(new Item.Properties()));
    public static final DeferredItem<Item> JERRY_CAN = REGISTER.register("jerry_can", () -> new JerryCanItem(() -> ((ModConfigSpec.IntValue)Config.SERVER.jerryCanCapacity).get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> INDUSTRIAL_JERRY_CAN = REGISTER.register("industrial_jerry_can", () -> new JerryCanItem(() -> ((ModConfigSpec.IntValue)Config.SERVER.industrialJerryCanCapacity).get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> WRENCH = REGISTER.register("wrench", () -> new WrenchItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> HAMMER = REGISTER.register("hammer", () -> new HammerItem(new Item.Properties().durability(200)));
    public static final DeferredItem<Item> KEY = REGISTER.register("key", () -> new KeyItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BucketItem> FUELIUM_BUCKET = REGISTER.register("fuelium_bucket", () -> new BucketItem((Fluid)ModFluids.FUELIUM.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<BucketItem> ENDER_SAP_BUCKET = REGISTER.register("ender_sap_bucket", () -> new BucketItem((Fluid)ModFluids.ENDER_SAP.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<BucketItem> BLAZE_JUICE_BUCKET = REGISTER.register("blaze_juice_bucket", () -> new BucketItem((Fluid)ModFluids.BLAZE_JUICE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
}

