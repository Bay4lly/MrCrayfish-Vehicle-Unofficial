package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModBlocks
{
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> TRAFFIC_CONE = REGISTER.register("traffic_cone", TrafficConeBlock::new);
    public static final RegistryObject<Block> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", FluidExtractorBlock::new);
    public static final RegistryObject<Block> FLUID_MIXER = REGISTER.register("fluid_mixer", FluidMixerBlock::new);
    public static final RegistryObject<Block> GAS_PUMP = REGISTER.register("gas_pump", GasPumpBlock::new);
    public static final RegistryObject<Block> FLUID_PIPE = REGISTER.register("fluid_pipe", FluidPipeBlock::new);
    public static final RegistryObject<Block> FLUID_PUMP = REGISTER.register("fluid_pump", FluidPumpBlock::new);
    public static final RegistryObject<FuelDrumBlock> FUEL_DRUM = REGISTER.register("fuel_drum", FuelDrumBlock::new);
    public static final RegistryObject<FuelDrumBlock> INDUSTRIAL_FUEL_DRUM = REGISTER.register("industrial_fuel_drum", IndustrialFuelDrumBlock::new);
    public static final RegistryObject<Block> WORKSTATION = REGISTER.register("workstation", WorkstationBlock::new);
    public static final RegistryObject<Block> VEHICLE_CRATE = REGISTER.register("vehicle_crate", VehicleCrateBlock::new);
    public static final RegistryObject<Block> JACK = REGISTER.register("jack", JackBlock::new);
    public static final RegistryObject<Block> JACK_HEAD = REGISTER.register("jack_head", JackHeadBlock::new);
    public static final RegistryObject<LiquidBlock> FUELIUM = REGISTER.register("fuelium", () -> new LiquidBlock(ModFluids.FLOWING_FUELIUM, BlockBehaviour.Properties.of().noCollission().forceSolidOff().pushReaction(PushReaction.DESTROY).replaceable().liquid().strength(100.0F).noLootTable().mapColor(MapColor.WATER)));
    public static final RegistryObject<LiquidBlock> ENDER_SAP = REGISTER.register("ender_sap", () -> new LiquidBlock(ModFluids.FLOWING_ENDER_SAP, BlockBehaviour.Properties.of().noCollission().forceSolidOff().pushReaction(PushReaction.DESTROY).strength(100.0F).noLootTable().mapColor(MapColor.WATER)));
    public static final RegistryObject<LiquidBlock> BLAZE_JUICE = REGISTER.register("blaze_juice", () -> new LiquidBlock(ModFluids.FLOWING_BLAZE_JUICE, BlockBehaviour.Properties.of().noCollission().forceSolidOff().pushReaction(PushReaction.DESTROY).strength(100.0F).noLootTable().mapColor(MapColor.WATER)));
    //public static final Block BOOST_PAD = registerConstructor(new BlockBoostPad(), null);
    //public static final Block BOOST_RAMP = registerConstructor(new BlockBoostRamp(), null); //ItemBoostRamp::new
    //public static final Block STEEP_BOOST_RAMP = registerConstructor(new BlockSteepBoostRamp(), null);
}