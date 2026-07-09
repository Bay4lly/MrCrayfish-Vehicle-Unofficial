/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.Container
 *  net.minecraft.world.flag.FeatureFlags
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.MenuType$MenuSupplier
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.inventory.container.FluidExtractorContainer;
import com.mrcrayfish.vehicle.inventory.container.FluidMixerContainer;
import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import com.mrcrayfish.vehicle.inventory.container.WorkstationContainer;
import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity;
import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.MENU, (String)"vehicle");
    public static final DeferredHolder<MenuType<?>, MenuType<FluidExtractorContainer>> FLUID_EXTRACTOR = ModContainers.register("fluid_extractor", (windowId, playerInventory, data) -> {
        FluidExtractorTileEntity fluidExtractor = (FluidExtractorTileEntity)playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new FluidExtractorContainer(windowId, (Container)playerInventory, fluidExtractor);
    });
    public static final DeferredHolder<MenuType<?>, MenuType<FluidMixerContainer>> FLUID_MIXER = ModContainers.register("fluid_mixer", (windowId, playerInventory, data) -> {
        FluidMixerTileEntity fluidMixer = (FluidMixerTileEntity)playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new FluidMixerContainer(windowId, playerInventory, fluidMixer);
    });
    public static final DeferredHolder<MenuType<?>, MenuType<EditVehicleContainer>> EDIT_VEHICLE = ModContainers.register("edit_vehicle", (windowId, playerInventory, data) -> {
        PoweredVehicleEntity entity = (PoweredVehicleEntity)playerInventory.player.level().getEntity(data.readInt());
        return new EditVehicleContainer(windowId, (Container)entity.getVehicleInventory(), entity, playerInventory.player, playerInventory);
    });
    public static final DeferredHolder<MenuType<?>, MenuType<WorkstationContainer>> WORKSTATION = ModContainers.register("workstation", (windowId, playerInventory, data) -> {
        WorkstationTileEntity workstation = (WorkstationTileEntity)playerInventory.player.level().getBlockEntity(data.readBlockPos());
        return new WorkstationContainer(windowId, (Container)playerInventory, workstation);
    });
    public static final DeferredHolder<MenuType<?>, MenuType<StorageContainer>> STORAGE = ModContainers.register("storage", (windowId, playerInventory, data) -> {
        IStorage storage = (IStorage)playerInventory.player.level().getEntity(data.readVarInt());
        return new StorageContainer(windowId, (Container)playerInventory, storage, playerInventory.player);
    });

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String id, net.neoforged.neoforge.network.IContainerFactory<T> factory) {
        return REGISTER.register(id, () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(factory));
    }
}

