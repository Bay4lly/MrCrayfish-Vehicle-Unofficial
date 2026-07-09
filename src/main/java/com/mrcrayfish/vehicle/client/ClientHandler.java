/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.item.ItemColor
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimplePreparableReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.fml.ModList
 *  net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.AddReloadListenerEvent
 */
package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.client.ClientEvents;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.handler.CameraHandler;
import com.mrcrayfish.vehicle.client.handler.ControllerHandler;
import com.mrcrayfish.vehicle.client.handler.FuelingHandler;
import com.mrcrayfish.vehicle.client.handler.HeldVehicleHandler;
import com.mrcrayfish.vehicle.client.handler.InputHandler;
import com.mrcrayfish.vehicle.client.handler.OverlayHandler;
import com.mrcrayfish.vehicle.client.handler.SprayCanHandler;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.screen.EditVehicleScreen;
import com.mrcrayfish.vehicle.client.screen.FluidExtractorScreen;
import com.mrcrayfish.vehicle.client.screen.FluidMixerScreen;
import com.mrcrayfish.vehicle.client.screen.StorageScreen;
import com.mrcrayfish.vehicle.client.screen.WorkstationScreen;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.init.ModDataComponents;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.item.PartItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class ClientHandler {
    private static boolean controllableLoaded = false;

    public static boolean isControllableLoaded() {
        return controllableLoaded;
    }

    public static void setup() {
        if (ModList.get().isLoaded("controllable")) {
            controllableLoaded = true;
            NeoForge.EVENT_BUS.register((Object)new ControllerHandler());
        }
        NeoForge.EVENT_BUS.register((Object)EntityRayTracer.instance());
        NeoForge.EVENT_BUS.register((Object)new CameraHandler());
        NeoForge.EVENT_BUS.register((Object)new FuelingHandler());
        NeoForge.EVENT_BUS.register((Object)new HeldVehicleHandler());
        NeoForge.EVENT_BUS.register((Object)new InputHandler());
        NeoForge.EVENT_BUS.register((Object)new OverlayHandler());
        NeoForge.EVENT_BUS.register((Object)new SprayCanHandler());
        NeoForge.EVENT_BUS.register((Object)new ClientEvents());
        ClientHandler.setupCustomBlockModels();
        ClientHandler.setupRenderLayers();
        ClientHandler.setupItemColors();
        NeoForge.EVENT_BUS.addListener(ClientHandler::onAddReloadListeners);
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)new SimplePreparableReloadListener<Void>(){

            protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
                return null;
            }

            protected void apply(Void object, ResourceManager manager, ProfilerFiller profiler) {
                FluidUtils.clearCacheFluidColor();
                EntityRayTracer.instance().clearDataForReregistration();
                SpecialModels.clearModelCache();
            }
        });
    }

    private static void setupCustomBlockModels() {
    }

    private static void setupRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)ModFluids.FUELIUM.get()), (RenderType)RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)ModFluids.FLOWING_FUELIUM.get()), (RenderType)RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)ModFluids.ENDER_SAP.get()), (RenderType)RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)ModFluids.FLOWING_ENDER_SAP.get()), (RenderType)RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)ModFluids.BLAZE_JUICE.get()), (RenderType)RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)ModFluids.FLOWING_BLAZE_JUICE.get()), (RenderType)RenderType.translucent());
    }

    public static void setupScreenFactories(RegisterMenuScreensEvent event) {
        event.register((MenuType)ModContainers.FLUID_EXTRACTOR.get(), FluidExtractorScreen::new);
        event.register((MenuType)ModContainers.FLUID_MIXER.get(), FluidMixerScreen::new);
        event.register((MenuType)ModContainers.EDIT_VEHICLE.get(), EditVehicleScreen::new);
        event.register((MenuType)ModContainers.WORKSTATION.get(), WorkstationScreen::new);
        event.register((MenuType)ModContainers.STORAGE.get(), StorageScreen::new);
    }

    private static void setupItemColors() {
        ItemColor color = (stack, index) -> {
            Integer c;
            if (index == 0 && (c = (Integer)stack.get((DataComponentType)ModDataComponents.COLOR.get())) != null) {
                return c;
            }
            return 0xFFFFFF;
        };
        BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof SprayCanItem || item instanceof PartItem && ((PartItem)((Object)item)).isColored()) {
                Minecraft.getInstance().getItemColors().register(color, new ItemLike[]{item});
            }
        });
    }

    public static class PropertiesSupplier {
        private VehicleProperties properties;

        private PropertiesSupplier(VehicleProperties properties) {
            this.properties = properties;
        }

        public VehicleProperties get() {
            return this.properties;
        }

        private static PropertiesSupplier of(VehicleProperties properties) {
            return new PropertiesSupplier(properties);
        }
    }
}

