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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
    private final IEventBus modEventBus;

    public VehicleMod(IEventBus eventBus, ModContainer container)
    {
        this.modEventBus = eventBus;
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
        ModDataComponents.REGISTER.register(eventBus);
        HeldVehicleDataHandler.registerBus(eventBus);
        ModLootFunctions.init(eventBus);
        container.registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        container.registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::onGatherData);
        if(net.neoforged.fml.loading.FMLEnvironment.dist.isClient())
        {
            eventBus.addListener(ClientHandler::setupScreenFactories);
            eventBus.register(com.mrcrayfish.vehicle.client.init.ModBlockEntityRenderers.class);
            eventBus.register(com.mrcrayfish.vehicle.client.init.KeyBinds.class);
        }
        eventBus.addListener(this::onRegisterCapabilities);
        eventBus.addListener(PacketHandler::register);
        NeoForge.EVENT_BUS.register(new CommonEvents());
        NeoForge.EVENT_BUS.register(FluidNetworkHandler.instance());
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        VehicleProperties.loadProperties();
        HeldVehicleDataHandler.register();
        ItemLookup.init();
        ModDataKeys.register();
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        ClientHandler.setup();
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(LootTableGen::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new VehiclePropertiesGen(packOutput));
    }

    private void onRegisterCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
            ModTileEntities.FLUID_MIXER.get(),
            (be, side) -> {
                if(side != null)
                {
                    net.minecraft.world.level.block.state.BlockState state = be.getBlockState();
                    if(state.getProperties().contains(com.mrcrayfish.vehicle.block.RotatedObjectBlock.DIRECTION))
                    {
                        net.minecraft.core.Direction direction = state.getValue(com.mrcrayfish.vehicle.block.RotatedObjectBlock.DIRECTION);
                        if(side == direction.getCounterClockWise())
                        {
                            return be.getBlazeTank();
                        }
                        if(side == direction)
                        {
                            return be.getEnderSapTank();
                        }
                        if(side == direction.getClockWise())
                        {
                            return be.getFueliumTank();
                        }
                    }
                }
                return null;
            }
        );

        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
            ModTileEntities.FLUID_EXTRACTOR.get(),
            (be, side) -> be.getFluidTank()
        );

        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
            ModTileEntities.FUEL_DRUM.get(),
            (be, side) -> be.getFluidTank()
        );

        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
            ModTileEntities.INDUSTRIAL_FUEL_DRUM.get(),
            (be, side) -> be.getFluidTank()
        );

        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
            ModTileEntities.GAS_PUMP_TANK.get(),
            (be, side) -> be.getFluidTank()
        );

        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
            ModTileEntities.FLUID_MIXER.get(),
            (be, side) -> new net.neoforged.neoforge.items.wrapper.InvWrapper(be)
        );

        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
            ModTileEntities.FLUID_EXTRACTOR.get(),
            (be, side) -> new net.neoforged.neoforge.items.wrapper.InvWrapper(be)
        );

        event.registerItem(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM,
            (stack, context) -> new net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack(ModDataComponents.FLUID_CONTENT, stack, ((com.mrcrayfish.vehicle.item.JerryCanItem)stack.getItem()).getCapacity()),
            ModItems.JERRY_CAN.get(),
            ModItems.INDUSTRIAL_JERRY_CAN.get()
        );
    }
}
