/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.loot.LootTableProvider
 *  net.minecraft.data.loot.LootTableProvider$SubProviderEntry
 *  net.minecraft.world.Container
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.fml.loading.FMLEnvironment
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.data.event.GatherDataEvent
 *  net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack
 *  net.neoforged.neoforge.items.wrapper.InvWrapper
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mrcrayfish.vehicle;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import com.mrcrayfish.vehicle.client.ClientHandler;
import com.mrcrayfish.vehicle.client.init.KeyBinds;
import com.mrcrayfish.vehicle.client.init.ModBlockEntityRenderers;
import com.mrcrayfish.vehicle.common.CommonEvents;
import com.mrcrayfish.vehicle.common.FluidNetworkHandler;
import com.mrcrayfish.vehicle.common.ItemLookup;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.datagen.LootTableGen;
import com.mrcrayfish.vehicle.datagen.RecipeGen;
import com.mrcrayfish.vehicle.datagen.VehiclePropertiesGen;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModContainers;
import com.mrcrayfish.vehicle.init.ModDataComponents;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModFluidTypes;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItemTabs;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModLootFunctions;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import com.mrcrayfish.vehicle.init.ModRecipeTypes;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.init.ModTileEntities;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.Container;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value="vehicle")
public class VehicleMod {
    public static final Logger LOGGER = LogManager.getLogger((String)"vehicle");
    private final IEventBus modEventBus;

    public VehicleMod(IEventBus eventBus, ModContainer container) {
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
        container.registerConfig(ModConfig.Type.SERVER, (IConfigSpec)Config.serverSpec);
        container.registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)Config.clientSpec);
        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::onGatherData);
        if (FMLEnvironment.dist.isClient()) {
            eventBus.addListener(ClientHandler::setupScreenFactories);
            eventBus.register(ModBlockEntityRenderers.class);
            eventBus.register(KeyBinds.class);
        }
        eventBus.addListener(this::onRegisterCapabilities);
        eventBus.addListener(PacketHandler::register);
        NeoForge.EVENT_BUS.register((Object)new CommonEvents());
        NeoForge.EVENT_BUS.register((Object)FluidNetworkHandler.instance());
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        VehicleProperties.loadProperties();
        HeldVehicleDataHandler.register();
        ItemLookup.init();
        ModDataKeys.register();
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        ClientHandler.setup();
    }

    private void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(event.includeServer(), (DataProvider)new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(LootTableGen::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));
        generator.addProvider(event.includeServer(), (DataProvider)new RecipeGen(packOutput, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), (DataProvider)new VehiclePropertiesGen(packOutput));
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)ModTileEntities.FLUID_MIXER.get(), (be, side) -> {
            BlockState state;
            if (side != null && (state = be.getBlockState()).getProperties().contains(RotatedObjectBlock.DIRECTION)) {
                Direction direction = (Direction)state.getValue((Property)RotatedObjectBlock.DIRECTION);
                if (side == direction.getCounterClockWise()) {
                    return ((com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity)be).getBlazeTank();
                }
                if (side == direction) {
                    return ((com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity)be).getEnderSapTank();
                }
                if (side == direction.getClockWise()) {
                    return ((com.mrcrayfish.vehicle.tileentity.FluidMixerTileEntity)be).getFueliumTank();
                }
            }
            return null;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)ModTileEntities.FLUID_EXTRACTOR.get(), (be, side) -> ((com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced)be).getFluidTank());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)ModTileEntities.FUEL_DRUM.get(), (be, side) -> ((com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced)be).getFluidTank());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)ModTileEntities.INDUSTRIAL_FUEL_DRUM.get(), (be, side) -> ((com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced)be).getFluidTank());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)ModTileEntities.GAS_PUMP_TANK.get(), (be, side) -> ((com.mrcrayfish.vehicle.tileentity.TileFluidHandlerSynced)be).getFluidTank());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)ModTileEntities.FLUID_MIXER.get(), (be, side) -> new InvWrapper((Container)be));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)ModTileEntities.FLUID_EXTRACTOR.get(), (be, side) -> new InvWrapper((Container)be));
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidHandlerItemStack(ModDataComponents.FLUID_CONTENT, stack, ((JerryCanItem)stack.getItem()).getCapacity()), new ItemLike[]{(ItemLike)ModItems.JERRY_CAN.get(), (ItemLike)ModItems.INDUSTRIAL_JERRY_CAN.get()});
    }
}

