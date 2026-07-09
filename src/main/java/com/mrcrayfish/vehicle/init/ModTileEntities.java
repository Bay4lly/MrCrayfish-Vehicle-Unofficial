/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.BoostTileEntity;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTankTileEntity;
import com.mrcrayfish.vehicle.tileentity.GasPumpTileEntity;
import com.mrcrayfish.vehicle.tileentity.IndustrialFuelDrumTileEntity;
import com.mrcrayfish.vehicle.tileentity.JackTileEntity;
import com.mrcrayfish.vehicle.tileentity.PipeTileEntity;
import com.mrcrayfish.vehicle.tileentity.PumpTileEntity;
import com.mrcrayfish.vehicle.tileentity.VehicleCrateTileEntity;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.BLOCK_ENTITY_TYPE, (String)"vehicle");
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidExtractorTileEntity>> FLUID_EXTRACTOR = ModTileEntities.register("fluid_extractor", FluidExtractorTileEntity::new, () -> new Block[]{(Block)ModBlocks.FLUID_EXTRACTOR.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeTileEntity>> FLUID_PIPE = ModTileEntities.register("fluid_pipe", PipeTileEntity::new, () -> new Block[]{(Block)ModBlocks.FLUID_PIPE.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PumpTileEntity>> FLUID_PUMP = ModTileEntities.register("fluid_pump", PumpTileEntity::new, () -> new Block[]{(Block)ModBlocks.FLUID_PUMP.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FuelDrumTileEntity>> FUEL_DRUM = ModTileEntities.register("fuel_drum", FuelDrumTileEntity::new, () -> new Block[]{(Block)ModBlocks.FUEL_DRUM.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IndustrialFuelDrumTileEntity>> INDUSTRIAL_FUEL_DRUM = ModTileEntities.register("industrial_fuel_drum", IndustrialFuelDrumTileEntity::new, () -> new Block[]{(Block)ModBlocks.INDUSTRIAL_FUEL_DRUM.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidMixerTileEntity>> FLUID_MIXER = ModTileEntities.register("fluid_mixer", FluidMixerTileEntity::new, () -> new Block[]{(Block)ModBlocks.FLUID_MIXER.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VehicleCrateTileEntity>> VEHICLE_CRATE = ModTileEntities.register("vehicle_crate", VehicleCrateTileEntity::new, () -> new Block[]{(Block)ModBlocks.VEHICLE_CRATE.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkstationTileEntity>> WORKSTATION = ModTileEntities.register("workstation", WorkstationTileEntity::new, () -> new Block[]{(Block)ModBlocks.WORKSTATION.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JackTileEntity>> JACK = ModTileEntities.register("jack", JackTileEntity::new, () -> new Block[]{(Block)ModBlocks.JACK.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoostTileEntity>> BOOST = ModTileEntities.register("boost", BoostTileEntity::new, () -> new Block[0]);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasPumpTileEntity>> GAS_PUMP = ModTileEntities.register("gas_pump", GasPumpTileEntity::new, () -> new Block[]{(Block)ModBlocks.GAS_PUMP.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasPumpTankTileEntity>> GAS_PUMP_TANK = ModTileEntities.register("gas_pump_tank", GasPumpTankTileEntity::new, () -> new Block[]{(Block)ModBlocks.GAS_PUMP.get()});

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String id, BlockEntityType.BlockEntitySupplier<T> factoryIn, Supplier<Block[]> validBlocksSupplier) {
        return REGISTER.register(id, () -> BlockEntityType.Builder.of((BlockEntityType.BlockEntitySupplier)factoryIn, (Block[])((Block[])validBlocksSupplier.get())).build(null));
    }
}

