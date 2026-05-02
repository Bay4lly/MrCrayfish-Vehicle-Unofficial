package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> TRAFFIC_CONE = REGISTER.register("traffic_cone", () -> new ItemTrafficCone(ModBlocks.TRAFFIC_CONE.get()));
    public static final RegistryObject<Item> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", () -> new BlockItem(ModBlocks.FLUID_EXTRACTOR.get(), new Properties()));
    public static final RegistryObject<Item> FLUID_MIXER = REGISTER.register("fluid_mixer", () -> new BlockItem(ModBlocks.FLUID_MIXER.get(), new Properties()));
    public static final RegistryObject<Item> GAS_PUMP = REGISTER.register("gas_pump", () -> new BlockItem(ModBlocks.GAS_PUMP.get(), new Properties()));
    public static final RegistryObject<Item> FLUID_PIPE = REGISTER.register("fluid_pipe", () -> new FluidPipeItem(ModBlocks.FLUID_PIPE.get()));
    public static final RegistryObject<Item> FLUID_PUMP = REGISTER.register("fluid_pump", () -> new FluidPipeItem(ModBlocks.FLUID_PUMP.get()));
    public static final RegistryObject<Item> FUEL_DRUM = REGISTER.register("fuel_drum", () -> new BlockItem(ModBlocks.FUEL_DRUM.get(), new Properties()));
    public static final RegistryObject<Item> INDUSTRIAL_FUEL_DRUM = REGISTER.register("industrial_fuel_drum", () -> new BlockItem(ModBlocks.INDUSTRIAL_FUEL_DRUM.get(), new Properties()));
    public static final RegistryObject<Item> WORKSTATION = REGISTER.register("workstation", () -> new BlockItem(ModBlocks.WORKSTATION.get(), new Properties()));
    public static final RegistryObject<Item> VEHICLE_CRATE = REGISTER.register("vehicle_crate", () -> new BlockItem(ModBlocks.VEHICLE_CRATE.get(), new Properties().stacksTo(1)));
    public static final RegistryObject<Item> JACK = REGISTER.register("jack", () -> new BlockItem(ModBlocks.JACK.get(), new Properties()));

    public static final RegistryObject<Item> PANEL = REGISTER.register("panel", () -> new Item(new Properties()));
    public static final RegistryObject<Item> STANDARD_WHEEL = REGISTER.register("standard_wheel", () -> new WheelItem(WheelType.STANDARD, new Properties()).setColored());
    public static final RegistryObject<Item> SPORTS_WHEEL = REGISTER.register("sports_wheel", () -> new WheelItem(WheelType.SPORTS, new Properties()).setColored());
    public static final RegistryObject<Item> RACING_WHEEL = REGISTER.register("racing_wheel", () -> new WheelItem(WheelType.RACING, new Properties()).setColored());
    public static final RegistryObject<Item> OFF_ROAD_WHEEL = REGISTER.register("off_road_wheel", () -> new WheelItem(WheelType.OFF_ROAD, new Properties()).setColored());
    public static final RegistryObject<Item> SNOW_WHEEL = REGISTER.register("snow_wheel", () -> new WheelItem(WheelType.SNOW, new Properties()).setColored());
    public static final RegistryObject<Item> ALL_TERRAIN_WHEEL = REGISTER.register("all_terrain_wheel", () -> new WheelItem(WheelType.ALL_TERRAIN, new Properties()).setColored());
    public static final RegistryObject<Item> PLASTIC_WHEEL = REGISTER.register("plastic_wheel", () -> new WheelItem(WheelType.PLASTIC, new Properties()));
    public static final RegistryObject<Item> IRON_SMALL_ENGINE = REGISTER.register("iron_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.IRON, new Properties()));
    public static final RegistryObject<Item> GOLD_SMALL_ENGINE = REGISTER.register("gold_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.GOLD, new Properties()));
    public static final RegistryObject<Item> DIAMOND_SMALL_ENGINE = REGISTER.register("diamond_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.DIAMOND, new Properties()));
    public static final RegistryObject<Item> NETHERITE_SMALL_ENGINE = REGISTER.register("netherite_small_engine", () -> new EngineItem(EngineType.SMALL_MOTOR, EngineTier.NETHERITE, new Properties()));
    public static final RegistryObject<Item> IRON_LARGE_ENGINE = REGISTER.register("iron_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.IRON, new Properties()));
    public static final RegistryObject<Item> GOLD_LARGE_ENGINE = REGISTER.register("gold_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.GOLD, new Properties()));
    public static final RegistryObject<Item> DIAMOND_LARGE_ENGINE = REGISTER.register("diamond_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.DIAMOND, new Properties()));
    public static final RegistryObject<Item> NETHERITE_LARGE_ENGINE = REGISTER.register("netherite_large_engine", () -> new EngineItem(EngineType.LARGE_MOTOR, EngineTier.NETHERITE, new Properties()));
    public static final RegistryObject<Item> IRON_ELECTRIC_ENGINE = REGISTER.register("iron_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.IRON, new Properties()));
    public static final RegistryObject<Item> GOLD_ELECTRIC_ENGINE = REGISTER.register("gold_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.GOLD, new Properties()));
    public static final RegistryObject<Item> DIAMOND_ELECTRIC_ENGINE = REGISTER.register("diamond_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.DIAMOND, new Properties()));
    public static final RegistryObject<Item> NETHERITE_ELECTRIC_ENGINE = REGISTER.register("netherite_electric_engine", () -> new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.NETHERITE, new Properties()));
    public static final RegistryObject<SprayCanItem> SPRAY_CAN = REGISTER.register("spray_can", () -> new SprayCanItem(new Properties()));
    public static final RegistryObject<Item> JERRY_CAN = REGISTER.register("jerry_can", () -> new JerryCanItem(Config.SERVER.jerryCanCapacity::get, new Properties().stacksTo(1)));
    public static final RegistryObject<Item> INDUSTRIAL_JERRY_CAN = REGISTER.register("industrial_jerry_can", () -> new JerryCanItem(Config.SERVER.industrialJerryCanCapacity::get, new Properties().stacksTo(1)));
    public static final RegistryObject<Item> WRENCH = REGISTER.register("wrench", () -> new WrenchItem(new Properties().stacksTo(1)));
    public static final RegistryObject<Item> HAMMER = REGISTER.register("hammer", () -> new HammerItem(new Properties().durability(200)));
    public static final RegistryObject<Item> KEY = REGISTER.register("key", () -> new KeyItem(new Properties().stacksTo(1)));
    public static final RegistryObject<BucketItem> FUELIUM_BUCKET = REGISTER.register("fuelium_bucket", () -> new BucketItem(ModFluids.FUELIUM, (new Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<BucketItem> ENDER_SAP_BUCKET = REGISTER.register("ender_sap_bucket", () -> new BucketItem(ModFluids.ENDER_SAP, (new Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<BucketItem> BLAZE_JUICE_BUCKET = REGISTER.register("blaze_juice_bucket", () -> new BucketItem(ModFluids.BLAZE_JUICE, (new Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));

}