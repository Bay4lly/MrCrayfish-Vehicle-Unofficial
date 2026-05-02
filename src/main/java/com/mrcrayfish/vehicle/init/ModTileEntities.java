package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.tileentity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModTileEntities
{
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Reference.MOD_ID);

    public static final RegistryObject<BlockEntityType<FluidExtractorTileEntity>> FLUID_EXTRACTOR = register("fluid_extractor", FluidExtractorTileEntity::new, () -> new Block[]{ModBlocks.FLUID_EXTRACTOR.get()});
    public static final RegistryObject<BlockEntityType<PipeTileEntity>> FLUID_PIPE = register("fluid_pipe", PipeTileEntity::new, () -> new Block[]{ModBlocks.FLUID_PIPE.get()});
    public static final RegistryObject<BlockEntityType<PumpTileEntity>> FLUID_PUMP = register("fluid_pump", PumpTileEntity::new, () -> new Block[]{ModBlocks.FLUID_PUMP.get()});
    public static final RegistryObject<BlockEntityType<FuelDrumTileEntity>> FUEL_DRUM = register("fuel_drum", FuelDrumTileEntity::new, () -> new Block[]{ModBlocks.FUEL_DRUM.get()});
    public static final RegistryObject<BlockEntityType<IndustrialFuelDrumTileEntity>> INDUSTRIAL_FUEL_DRUM = register("industrial_fuel_drum", IndustrialFuelDrumTileEntity::new, () -> new Block[]{ModBlocks.INDUSTRIAL_FUEL_DRUM.get()});
    public static final RegistryObject<BlockEntityType<FluidMixerTileEntity>> FLUID_MIXER = register("fluid_mixer", FluidMixerTileEntity::new, () -> new Block[]{ModBlocks.FLUID_MIXER.get()});
    public static final RegistryObject<BlockEntityType<VehicleCrateTileEntity>> VEHICLE_CRATE = register("vehicle_crate", VehicleCrateTileEntity::new, () -> new Block[]{ModBlocks.VEHICLE_CRATE.get()});
    public static final RegistryObject<BlockEntityType<WorkstationTileEntity>> WORKSTATION = register("workstation", WorkstationTileEntity::new, () -> new Block[]{ModBlocks.WORKSTATION.get()});
    public static final RegistryObject<BlockEntityType<JackTileEntity>> JACK = register("jack", JackTileEntity::new, () -> new Block[]{ModBlocks.JACK.get()});
    public static final RegistryObject<BlockEntityType<BoostTileEntity>> BOOST = register("boost", BoostTileEntity::new, () -> new Block[]{});
    public static final RegistryObject<BlockEntityType<GasPumpTileEntity>> GAS_PUMP = register("gas_pump", GasPumpTileEntity::new, () -> new Block[]{ModBlocks.GAS_PUMP.get()});
    public static final RegistryObject<BlockEntityType<GasPumpTankTileEntity>> GAS_PUMP_TANK = register("gas_pump_tank", GasPumpTankTileEntity::new, () -> new Block[]{ModBlocks.GAS_PUMP.get()});

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String id, BlockEntitySupplier<T> factoryIn, Supplier<Block[]> validBlocksSupplier)
    {
        return REGISTER.register(id, () -> BlockEntityType.Builder.of(factoryIn, validBlocksSupplier.get()).build(null));
    }
}