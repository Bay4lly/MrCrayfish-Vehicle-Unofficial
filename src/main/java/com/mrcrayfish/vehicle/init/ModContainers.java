package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.inventory.container.*;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Reference.MOD_ID);

    public static final RegistryObject<MenuType<FluidExtractorContainer>> FLUID_EXTRACTOR = register("fluid_extractor", (IContainerFactory<FluidExtractorContainer>) (windowId, playerInventory, data) -> {
        FluidExtractorTileEntity fluidExtractor = (FluidExtractorTileEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new FluidExtractorContainer(windowId, playerInventory, fluidExtractor);
    });
    public static final RegistryObject<MenuType<FluidMixerContainer>> FLUID_MIXER = register("fluid_mixer", (IContainerFactory<FluidMixerContainer>) (windowId, playerInventory, data) -> {
        FluidMixerTileEntity fluidMixer = (FluidMixerTileEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new FluidMixerContainer(windowId, playerInventory, fluidMixer);
    });
    public static final RegistryObject<MenuType<EditVehicleContainer>> EDIT_VEHICLE = register("edit_vehicle", (IContainerFactory<EditVehicleContainer>) (windowId, playerInventory, data) -> {
        PoweredVehicleEntity entity = (PoweredVehicleEntity) playerInventory.player.level().getEntity(data.readInt());
        return new EditVehicleContainer(windowId, entity.getVehicleInventory(), entity, playerInventory.player, playerInventory);
    });
    public static final RegistryObject<MenuType<WorkstationContainer>> WORKSTATION = register("workstation", (IContainerFactory<WorkstationContainer>) (windowId, playerInventory, data) -> {
        WorkstationTileEntity workstation = (WorkstationTileEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new WorkstationContainer(windowId, playerInventory, workstation);
    });
    public static final RegistryObject<MenuType<StorageContainer>> STORAGE = register("storage", (IContainerFactory<StorageContainer>) (windowId, playerInventory, data) -> {
        IStorage storage = (IStorage) playerInventory.player.level().getEntity(data.readVarInt());
        return new StorageContainer(windowId, playerInventory, storage, playerInventory.player);
    });

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuType.MenuSupplier<T> factory)
    {
        return REGISTER.register(id, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }
}