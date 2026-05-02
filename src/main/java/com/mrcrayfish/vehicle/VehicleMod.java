package com.mrcrayfish.vehicle;

import com.mrcrayfish.vehicle.client.ClientHandler;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.FluidNetworkHandler;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.datagen.LootTableGen;
import com.mrcrayfish.vehicle.datagen.RecipeGen;
import com.mrcrayfish.vehicle.datagen.VehiclePropertiesGen;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.*;
import com.mrcrayfish.vehicle.network.PacketHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class VehicleMod
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public VehicleMod(FMLJavaModLoadingContext context)
    {
        IEventBus eventBus = context.getModEventBus();
        ModBlocks.REGISTER.register(eventBus);
        ModItems.REGISTER.register(eventBus);
        ModItemTabs.REGISTER.register(eventBus);
        ModEntities.REGISTER.register(eventBus);
        ModTileEntities.REGISTER.register(eventBus);
        ModContainers.REGISTER.register(eventBus);
        ModSounds.REGISTER.register(eventBus);
        ModRecipeTypes.REGISTER.register(eventBus);
        ModRecipeSerializers.REGISTER.register(eventBus);
        ModFluids.REGISTER.register(eventBus);
        ModFluidTypes.REGISTER.register(eventBus);
        context.registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        context.registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::onGatherData);
        eventBus.addListener(ModIngredients::register);
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        MinecraftForge.EVENT_BUS.register(FluidNetworkHandler.instance());
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        VehicleProperties.loadProperties();
        PacketHandler.register();
        HeldVehicleDataHandler.register();
        ItemLookup.init();
        ModDataKeys.register();
        ModLootFunctions.init();
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        ClientHandler.setup();
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(LootTableGen::new, LootContextParamSets.BLOCK))));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput));
        generator.addProvider(event.includeServer(), new VehiclePropertiesGen(packOutput));
    }
}
