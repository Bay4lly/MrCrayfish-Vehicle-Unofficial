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
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

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
            MinecraftForge.EVENT_BUS.register(new ControllerHandler());
        }

        MinecraftForge.EVENT_BUS.register(EntityRayTracer.instance());
        MinecraftForge.EVENT_BUS.register(new CameraHandler());
        MinecraftForge.EVENT_BUS.register(new FuelingHandler());
        MinecraftForge.EVENT_BUS.register(new HeldVehicleHandler());
        MinecraftForge.EVENT_BUS.register(new InputHandler());
        MinecraftForge.EVENT_BUS.register(new OverlayHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerModelHandler());
        MinecraftForge.EVENT_BUS.register(new SprayCanHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new ModBlockEntityRenderers());
        MinecraftForge.EVENT_BUS.register(new KeyBinds());

        setupCustomBlockModels();
        setupRenderLayers();
        setupScreenFactories();
        setupItemColors();

        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        if(manager instanceof ReloadableResourceManager)
        {
            ((ReloadableResourceManager) manager).registerReloadListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
                return stage.wait(Unit.INSTANCE).thenRun(() -> {
                    FluidUtils.clearCacheFluidColor();
                    EntityRayTracer.instance().clearDataForReregistration();
                    SpecialModels.clearModelCache();
                });
            });
        }
    }

    private static void setupCustomBlockModels()
    {
        //TODO add custom loader
        //ModelLoaderRegistry.registerLoader(new CustomLoader());
        //ModelLoaderRegistry.registerLoader(new ResourceLocation(Reference.MOD_ID, "ramp"), new CustomLoader());
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

    private static void setupScreenFactories()
    {
        MenuScreens.register(ModContainers.FLUID_EXTRACTOR.get(), FluidExtractorScreen::new);
        MenuScreens.register(ModContainers.FLUID_MIXER.get(), FluidMixerScreen::new);
        MenuScreens.register(ModContainers.EDIT_VEHICLE.get(), EditVehicleScreen::new);
        MenuScreens.register(ModContainers.WORKSTATION.get(), WorkstationScreen::new);
        MenuScreens.register(ModContainers.STORAGE.get(), StorageScreen::new);
    }

    private static void setupItemColors()
    {
        ItemColor color = (stack, index) ->
        {
            if(index == 0 && stack.hasTag() && stack.getTag().contains("Color", Tag.TAG_INT))
            {
                return stack.getTag().getInt("Color");
            }
            return 0xFFFFFF;
        };

        ForgeRegistries.ITEMS.forEach(item ->
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
