package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.client.handler.*;
import com.mrcrayfish.vehicle.client.init.KeyBinds;
import com.mrcrayfish.vehicle.client.init.ModBlockEntityRenderers;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.screen.*;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.item.PartItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.util.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.ModList;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    private static boolean controllableLoaded = false;

    public static boolean isControllableLoaded()
    {
        return controllableLoaded;
    }

    public static void setup()
    {
        if(ModList.get().isLoaded("controllable"))
        {
            ClientHandler.controllableLoaded = true;
            NeoForge.EVENT_BUS.register(new ControllerHandler());
        }

        NeoForge.EVENT_BUS.register(EntityRayTracer.instance());
        NeoForge.EVENT_BUS.register(new CameraHandler());
        NeoForge.EVENT_BUS.register(new FuelingHandler());
        NeoForge.EVENT_BUS.register(new HeldVehicleHandler());
        NeoForge.EVENT_BUS.register(new InputHandler());
        NeoForge.EVENT_BUS.register(new OverlayHandler());
        NeoForge.EVENT_BUS.register(new SprayCanHandler());
        NeoForge.EVENT_BUS.register(new ClientEvents());
        // Moved to mod bus in VehicleMod

        setupCustomBlockModels();
        setupRenderLayers();
        setupItemColors();

        NeoForge.EVENT_BUS.addListener(ClientHandler::onAddReloadListeners);
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(new net.minecraft.server.packs.resources.SimplePreparableReloadListener<Void>()
        {
            @Override
            protected Void prepare(net.minecraft.server.packs.resources.ResourceManager manager, net.minecraft.util.profiling.ProfilerFiller profiler)
            {
                return null;
            }

            @Override
            protected void apply(Void object, net.minecraft.server.packs.resources.ResourceManager manager, net.minecraft.util.profiling.ProfilerFiller profiler)
            {
                FluidUtils.clearCacheFluidColor();
                EntityRayTracer.instance().clearDataForReregistration();
                SpecialModels.clearModelCache();
            }
        });
    }

    private static void setupCustomBlockModels()
    {
        //TODO add custom loader
        //ModelLoaderRegistry.registerLoader(new CustomLoader());
        //ModelLoaderRegistry.registerLoader(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ramp"), new CustomLoader());
    }

    private static void setupRenderLayers()
    {
        ItemBlockRenderTypes.setRenderLayer(ModFluids.FUELIUM.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_FUELIUM.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.ENDER_SAP.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_ENDER_SAP.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.BLAZE_JUICE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_BLAZE_JUICE.get(), RenderType.translucent());
    }

    public static void setupScreenFactories(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.FLUID_EXTRACTOR.get(), FluidExtractorScreen::new);
        event.register(ModContainers.FLUID_MIXER.get(), FluidMixerScreen::new);
        event.register(ModContainers.EDIT_VEHICLE.get(), EditVehicleScreen::new);
        event.register(ModContainers.WORKSTATION.get(), WorkstationScreen::new);
        event.register(ModContainers.STORAGE.get(), StorageScreen::new);
    }

    private static void setupItemColors()
    {
        ItemColor color = (stack, index) ->
        {
            if(index == 0)
            {
                Integer c = stack.get(com.mrcrayfish.vehicle.init.ModDataComponents.COLOR.get());
                if(c != null) return c;
            }
            return 0xFFFFFF;
        };

        BuiltInRegistries.ITEM.forEach(item ->
        {
            if(item instanceof SprayCanItem || (item instanceof PartItem && ((PartItem) item).isColored()))
            {
                Minecraft.getInstance().getItemColors().register(color, item);
            }
        });
    }

    public static class PropertiesSupplier
    {
        private VehicleProperties properties;

        private PropertiesSupplier(VehicleProperties properties)
        {
            this.properties = properties;
        }

        public VehicleProperties get()
        {
            return this.properties;
        }

        private static PropertiesSupplier of(VehicleProperties properties)
        {
            return new PropertiesSupplier(properties);
        }
    }
}
