package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModTileEntities
{
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidExtractorTileEntity>> FLUID_EXTRACTOR = register("fluid_extractor", FluidExtractorTileEntity::new, () -> new Block[]{ModBlocks.FLUID_EXTRACTOR.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeTileEntity>> FLUID_PIPE = register("fluid_pipe", PipeTileEntity::new, () -> new Block[]{ModBlocks.FLUID_PIPE.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PumpTileEntity>> FLUID_PUMP = register("fluid_pump", PumpTileEntity::new, () -> new Block[]{ModBlocks.FLUID_PUMP.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FuelDrumTileEntity>> FUEL_DRUM = register("fuel_drum", FuelDrumTileEntity::new, () -> new Block[]{ModBlocks.FUEL_DRUM.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IndustrialFuelDrumTileEntity>> INDUSTRIAL_FUEL_DRUM = register("industrial_fuel_drum", IndustrialFuelDrumTileEntity::new, () -> new Block[]{ModBlocks.INDUSTRIAL_FUEL_DRUM.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidMixerTileEntity>> FLUID_MIXER = register("fluid_mixer", FluidMixerTileEntity::new, () -> new Block[]{ModBlocks.FLUID_MIXER.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VehicleCrateTileEntity>> VEHICLE_CRATE = register("vehicle_crate", VehicleCrateTileEntity::new, () -> new Block[]{ModBlocks.VEHICLE_CRATE.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkstationTileEntity>> WORKSTATION = register("workstation", WorkstationTileEntity::new, () -> new Block[]{ModBlocks.WORKSTATION.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JackTileEntity>> JACK = register("jack", JackTileEntity::new, () -> new Block[]{ModBlocks.JACK.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoostTileEntity>> BOOST = register("boost", BoostTileEntity::new, () -> new Block[]{});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasPumpTileEntity>> GAS_PUMP = register("gas_pump", GasPumpTileEntity::new, () -> new Block[]{ModBlocks.GAS_PUMP.get()});
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasPumpTankTileEntity>> GAS_PUMP_TANK = register("gas_pump_tank", GasPumpTankTileEntity::new, () -> new Block[]{ModBlocks.GAS_PUMP.get()});

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String id, BlockEntitySupplier<T> factoryIn, Supplier<Block[]> validBlocksSupplier)
    {
        return REGISTER.register(id, () -> BlockEntityType.Builder.of(factoryIn, validBlocksSupplier.get()).build(null));
    }
}