/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.level.material.PushReaction
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.block.FluidExtractorBlock;
import com.mrcrayfish.vehicle.block.FluidMixerBlock;
import com.mrcrayfish.vehicle.block.FluidPipeBlock;
import com.mrcrayfish.vehicle.block.FluidPumpBlock;
import com.mrcrayfish.vehicle.block.FuelDrumBlock;
import com.mrcrayfish.vehicle.block.GasPumpBlock;
import com.mrcrayfish.vehicle.block.IndustrialFuelDrumBlock;
import com.mrcrayfish.vehicle.block.JackBlock;
import com.mrcrayfish.vehicle.block.JackHeadBlock;
import com.mrcrayfish.vehicle.block.TrafficConeBlock;
import com.mrcrayfish.vehicle.block.VehicleCrateBlock;
import com.mrcrayfish.vehicle.block.WorkstationBlock;
import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.BLOCK, (String)"vehicle");
    public static final DeferredHolder<Block, Block> TRAFFIC_CONE = REGISTER.register("traffic_cone", TrafficConeBlock::new);
    public static final DeferredHolder<Block, Block> FLUID_EXTRACTOR = REGISTER.register("fluid_extractor", FluidExtractorBlock::new);
    public static final DeferredHolder<Block, Block> FLUID_MIXER = REGISTER.register("fluid_mixer", FluidMixerBlock::new);
    public static final DeferredHolder<Block, Block> GAS_PUMP = REGISTER.register("gas_pump", GasPumpBlock::new);
    public static final DeferredHolder<Block, Block> FLUID_PIPE = REGISTER.register("fluid_pipe", FluidPipeBlock::new);
    public static final DeferredHolder<Block, Block> FLUID_PUMP = REGISTER.register("fluid_pump", FluidPumpBlock::new);
    public static final DeferredHolder<Block, FuelDrumBlock> FUEL_DRUM = REGISTER.register("fuel_drum", FuelDrumBlock::new);
    public static final DeferredHolder<Block, FuelDrumBlock> INDUSTRIAL_FUEL_DRUM = REGISTER.register("industrial_fuel_drum", IndustrialFuelDrumBlock::new);
    public static final DeferredHolder<Block, Block> WORKSTATION = REGISTER.register("workstation", WorkstationBlock::new);
    public static final DeferredHolder<Block, Block> VEHICLE_CRATE = REGISTER.register("vehicle_crate", VehicleCrateBlock::new);
    public static final DeferredHolder<Block, Block> JACK = REGISTER.register("jack", JackBlock::new);
    public static final DeferredHolder<Block, Block> JACK_HEAD = REGISTER.register("jack_head", JackHeadBlock::new);
    public static final DeferredHolder<Block, LiquidBlock> FUELIUM = REGISTER.register("fuelium", () -> new LiquidBlock((FlowingFluid)ModFluids.FLOWING_FUELIUM.get(), BlockBehaviour.Properties.of().noCollission().forceSolidOff().pushReaction(PushReaction.DESTROY).replaceable().liquid().strength(100.0f).noLootTable().mapColor(MapColor.WATER)));
    public static final DeferredHolder<Block, LiquidBlock> ENDER_SAP = REGISTER.register("ender_sap", () -> new LiquidBlock((FlowingFluid)ModFluids.FLOWING_ENDER_SAP.get(), BlockBehaviour.Properties.of().noCollission().forceSolidOff().pushReaction(PushReaction.DESTROY).strength(100.0f).noLootTable().mapColor(MapColor.WATER)));
    public static final DeferredHolder<Block, LiquidBlock> BLAZE_JUICE = REGISTER.register("blaze_juice", () -> new LiquidBlock((FlowingFluid)ModFluids.FLOWING_BLAZE_JUICE.get(), BlockBehaviour.Properties.of().noCollission().forceSolidOff().pushReaction(PushReaction.DESTROY).strength(100.0f).noLootTable().mapColor(MapColor.WATER)));
}

